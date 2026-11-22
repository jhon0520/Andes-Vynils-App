#!/usr/bin/env python3
"""Walks the app with TalkBack (A11Y-02) and writes what TalkBack says after each step.

TalkBack ignores gestures injected with `adb shell input`, so the script drives it with its own keyboard
shortcuts through a virtual HID keyboard (`adb shell hid -`): Action (Meta) + Right/Left moves to the
next/previous element and Action + Space activates it. Focus order and announcements are the same as with
swipe gestures. The spoken text is read from logcat, so TalkBack's log level must be VERBOSE
(TalkBack settings > Advanced > Developer settings > Log output level).

Usage:
  talkback_walk.py --serial <serial> --out transcript.txt <step>...

Steps:
  note:<title>      section title in the transcript
  launch[:fresh]    (re)start the app; "fresh" clears its data first
  next[:n] prev[:n] move the TalkBack focus
  to:<text>         move forward until TalkBack says something containing <text> (back-to: moves backward)
  click             activate the focused element
  back              system back key
  type:<text>       type into the focused field (%s is a space)
  del:<n>           delete n characters
  wait:<seconds>
"""
import argparse
import json
import re
import subprocess
import time

PACKAGE = 'com.dev.wine.music.app'
TALKBACK = 'com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService'
ACTION = 0x08  # TalkBack's "Action" modifier is the Meta (Search) key
KEY_RIGHT, KEY_LEFT, KEY_SPACE = 0x4F, 0x50, 0x2C
# Standard HID boot keyboard descriptor (8-byte reports: modifiers, reserved, 6 key codes).
KEYBOARD_DESCRIPTOR = bytes.fromhex(
    '05010906a101050719e029e71500250175019508810295017508810195057501050819012905910295017503910195067508'
    '150025650507190029658100c0'
)
MAX_MOVES = 40


class Device:
    def __init__(self, serial):
        self.serial = serial
        self.keyboard = None

    def adb(self, *args):
        return subprocess.run(['adb', '-s', self.serial, *args], capture_output=True, text=True).stdout

    def start(self):
        self.keyboard = subprocess.Popen(['adb', '-s', self.serial, 'shell', 'hid', '-'],
                                         stdin=subprocess.PIPE, text=True)
        self._send({'id': 1, 'command': 'register', 'name': 'A11Y-02 keyboard', 'vid': 0x18D1, 'pid': 0x1234,
                    'bus': 'usb', 'descriptor': list(KEYBOARD_DESCRIPTOR)})
        self.adb('shell', 'settings', 'put', 'secure', 'enabled_accessibility_services', TALKBACK)
        self.adb('shell', 'settings', 'put', 'secure', 'accessibility_enabled', '1')
        time.sleep(5)

    def stop(self):
        self.adb('shell', 'settings', 'delete', 'secure', 'enabled_accessibility_services')
        self.adb('shell', 'settings', 'put', 'secure', 'accessibility_enabled', '0')
        if self.keyboard:
            self.keyboard.stdin.close()
            self.keyboard.terminate()

    def _send(self, command):
        self.keyboard.stdin.write(json.dumps(command) + '\n')
        self.keyboard.stdin.flush()

    def key(self, modifiers, code):
        report = lambda m, c: {'id': 1, 'command': 'report', 'report': [m, 0, c, 0, 0, 0, 0, 0]}
        for command in (report(modifiers, code), {'id': 1, 'command': 'delay', 'duration': 60},
                        report(modifiers, 0), {'id': 1, 'command': 'delay', 'duration': 40}, report(0, 0)):
            self._send(command)

    def spoken(self):
        log = '\n'.join(line for line in self.adb('logcat', '-d').split('\n') if ' talkback: ' in line)
        said = []
        for match in re.finditer(r'Speaking fragment text="([^"\n]*)', log):
            text = match.group(1).strip()
            if text and (not said or said[-1] != text):
                said.append(text)
        return ' | '.join(said)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--serial', required=True)
    parser.add_argument('--out', required=True)
    parser.add_argument('steps', nargs='+')
    args = parser.parse_args()
    device = Device(args.serial)
    out = open(args.out, 'a', encoding='utf-8')

    def step(label, action, wait=1.6):
        device.adb('logcat', '-c')
        action()
        time.sleep(wait)
        said = device.spoken()
        line = f'{label:<14} → {said or "(silence)"}'
        print(line)
        out.write(line + '\n')
        out.flush()
        return said

    device.start()
    try:
        for raw in args.steps:
            op, _, arg = raw.partition(':')
            count = int(arg) if arg.isdigit() else 1
            if op == 'note':
                out.write(f'\n## {arg}\n')
            elif op == 'launch':
                device.adb('shell', 'am', 'force-stop', PACKAGE)
                if arg == 'fresh':
                    device.adb('shell', 'pm', 'clear', PACKAGE)
                step('open app', lambda: device.adb('shell', 'am', 'start', '-n', f'{PACKAGE}/.MainActivity'), wait=5)
            elif op in ('next', 'prev'):
                code = KEY_RIGHT if op == 'next' else KEY_LEFT
                for _ in range(count):
                    step('next' if op == 'next' else 'previous', lambda: device.key(ACTION, code))
            elif op in ('to', 'back-to'):
                code = KEY_RIGHT if op == 'to' else KEY_LEFT
                for _ in range(MAX_MOVES):
                    if arg in step('next' if op == 'to' else 'previous', lambda: device.key(ACTION, code)):
                        break
            elif op == 'click':
                step('activate', lambda: device.key(ACTION, KEY_SPACE), wait=2.5)
            elif op == 'back':
                step('back', lambda: device.adb('shell', 'input', 'keyevent', '4'), wait=2.5)
            elif op == 'type':
                step(f'type «{arg.replace("%s", " ")}»', lambda: device.adb('shell', 'input', 'text', arg), wait=2.5)
            elif op == 'del':
                for _ in range(count):
                    device.adb('shell', 'input', 'keyevent', '67')
            elif op == 'wait':
                time.sleep(float(arg))
    finally:
        device.stop()


if __name__ == '__main__':
    main()
