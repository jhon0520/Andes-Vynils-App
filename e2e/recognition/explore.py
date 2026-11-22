#!/usr/bin/env python3
"""
Systematic exploration of the app (reconnaissance testing), driven with adb and uiautomator.

From the first screen it taps every clickable element of every screen it reaches, once per
screen. To go back to a screen it relaunches the app and replays the taps that led there, so
the order is deterministic and does not depend on the back button. Each new screen is saved
with a screenshot; a tap that closes the app (crash) or freezes it (ANR) is recorded with the
path that reproduces it.

The ids come from the Compose testTags (testTagsAsResourceId = true). Ids that end in a number
(album_item_100) are grouped into one family and only the first MAX_PER_FAMILY of each family
are tapped, so a long list does not take over the run. Buttons that send data to the backend
are skipped (SKIP), so the run never writes to the database.

Usage:
  e2e/recognition/explore.py --serial emulator-5554 --out e2e/recognition/results/systematic/emulator
"""
import argparse
import json
import re
import subprocess
import time
import xml.etree.ElementTree as ET
from pathlib import Path

PACKAGE = "com.dev.wine.music.app"
ACTIVITY = f"{PACKAGE}/.MainActivity"
SKIP = {"album_create_submit", "add_track_submit"}
MAX_PER_FAMILY = 2
NUMBER_SUFFIX = re.compile(r"_\d+$")


class Device:
    def __init__(self, serial):
        self.serial = serial

    def adb(self, *args, check=False):
        result = subprocess.run(["adb", "-s", self.serial, *args], capture_output=True, text=True)
        if check and result.returncode != 0:
            raise RuntimeError(result.stderr)
        return result.stdout

    def launch(self):
        self.adb("shell", "am", "start", "-S", "-W", "-n", ACTIVITY)
        time.sleep(2.5)

    def pid(self):
        return self.adb("shell", "pidof", PACKAGE).strip()

    def tap(self, x, y):
        self.adb("shell", "input", "tap", str(x), str(y))
        time.sleep(2.0)

    def type_text(self, text):
        self.adb("shell", "input", "text", text)
        self.adb("shell", "input", "keyevent", "111")  # ESCAPE hides the keyboard
        time.sleep(1.0)

    def dump(self):
        self.adb("shell", "uiautomator", "dump", "/sdcard/explore.xml")
        xml = self.adb("shell", "cat", "/sdcard/explore.xml")
        start = xml.find("<?xml")
        return ET.fromstring(xml[start:]) if start >= 0 else None

    def screenshot(self, path):
        with open(path, "wb") as out:
            subprocess.run(["adb", "-s", self.serial, "exec-out", "screencap", "-p"], stdout=out)

    def crash_log(self):
        return self.adb("logcat", "-d", "-b", "crash")

    def anr_events(self):
        return [line for line in self.adb("logcat", "-d", "-b", "events").splitlines() if "am_anr" in line]


def bounds_center(bounds):
    x1, y1, x2, y2 = map(int, re.findall(r"\d+", bounds))
    return (x1 + x2) // 2, (y1 + y2) // 2


def app_nodes(root):
    return [n for n in root.iter("node") if n.get("package") == PACKAGE]


def element_key(node):
    rid = node.get("resource-id", "")
    if rid:
        return rid
    label = node.get("text") or node.get("content-desc") or ""
    return f"{node.get('class', '').split('.')[-1]}:{label}" if label else ""


def screen_signature(root):
    """Ids without their number, so every album detail is the same screen whatever the album."""
    ids = {NUMBER_SUFFIX.sub("_N", n.get("resource-id")) for n in app_nodes(root) if n.get("resource-id")}
    # Ids of list items depend on what fits on screen; screens are told apart by their containers.
    return "|".join(sorted(i for i in ids if not i.endswith("_N")))


def actions_of(root):
    """Clickable or editable elements of the screen, at most MAX_PER_FAMILY per numbered family."""
    families = {}
    actions = []
    for node in app_nodes(root):
        editable = node.get("class", "").endswith("EditText")
        if node.get("enabled") != "true" or not (node.get("clickable") == "true" or editable):
            continue
        key = element_key(node)
        if not key or key in SKIP:
            continue
        family = NUMBER_SUFFIX.sub("_N", key)
        if family != key:
            families[family] = families.get(family, 0) + 1
            if families[family] > MAX_PER_FAMILY:
                continue
        actions.append({"key": key, "center": bounds_center(node.get("bounds")), "edit": editable})
    return actions


def find(root, key):
    for node in app_nodes(root):
        if element_key(node) == key:
            return bounds_center(node.get("bounds"))
    return None


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--serial", required=True)
    parser.add_argument("--out", required=True)
    parser.add_argument("--max-actions", type=int, default=150)
    args = parser.parse_args()

    out = Path(args.out)
    (out / "screens").mkdir(parents=True, exist_ok=True)
    device = Device(args.serial)
    device.adb("logcat", "-c")
    device.adb("logcat", "-b", "crash", "-c")
    device.adb("logcat", "-b", "events", "-c")

    screens = {}  # signature -> {"name", "path"}
    log = []
    issues = []
    pending = []  # (path to the screen, action) still to try
    seen_actions = set()

    def register(root, path):
        signature = screen_signature(root)
        if signature not in screens:
            name = f"S{len(screens):02d}"
            screens[signature] = {"name": name, "path": path, "ids": signature.split("|")}
            device.screenshot(out / "screens" / f"{name}.png")
        # Also on a known screen: a tap can enable new actions there (choosing a collector
        # enables «Continuar»), and they are reached through this longer path.
        for action in actions_of(root):
            if (signature, action["key"]) not in seen_actions:
                seen_actions.add((signature, action["key"]))
                pending.append((path, action))
        return screens[signature]["name"]

    def replay(path):
        """Relaunches the app and repeats the taps of [path]; returns the last tree or None."""
        device.launch()
        root = device.dump()
        for key in path:
            target = find(root, key) if root is not None else None
            if target is None:
                return None
            device.tap(*target)
            root = device.dump()
        return root

    device.launch()
    register(device.dump(), [])

    count = 0
    while pending and count < args.max_actions:
        path, action = pending.pop(0)
        root = replay(path)
        if root is None:
            log.append({"path": path, "action": action["key"], "result": "path not reproducible"})
            continue
        target = find(root, action["key"])
        if target is None:
            log.append({"path": path, "action": action["key"], "result": "element not on screen"})
            continue
        count += 1
        device.tap(*target)
        if action["edit"]:
            device.type_text("e2e")
        alive = device.pid() != ""
        anrs = device.anr_events()
        if not alive or anrs:
            kind = "crash" if not alive else "anr"
            issues.append({"type": kind, "path": path + [action["key"]], "log": device.crash_log()[-4000:] or "\n".join(anrs)})
            log.append({"path": path, "action": action["key"], "result": kind})
            device.adb("logcat", "-b", "crash", "-c")
            device.adb("logcat", "-b", "events", "-c")
            continue
        after = device.dump()
        if after is None:
            log.append({"path": path, "action": action["key"], "result": "no dump"})
            continue
        foreground = any(n.get("package") == PACKAGE for n in after.iter("node"))
        name = register(after, path + [action["key"]]) if foreground else "outside the app"
        log.append({"path": path, "action": action["key"], "result": name})

    summary = {
        "serial": args.serial,
        "model": device.adb("shell", "getprop", "ro.product.model").strip(),
        "android": device.adb("shell", "getprop", "ro.build.version.release").strip(),
        "actions": count,
        "pending_when_stopped": len(pending),
        "screens": [{"name": s["name"], "path": s["path"], "ids": s["ids"]} for s in screens.values()],
        "issues": issues,
        "log": log,
    }
    (out / "exploration.json").write_text(json.dumps(summary, indent=2, ensure_ascii=False))
    print(f"{count} actions, {len(screens)} screens, {len(issues)} crashes/ANRs, {len(pending)} actions left")


if __name__ == "__main__":
    main()
