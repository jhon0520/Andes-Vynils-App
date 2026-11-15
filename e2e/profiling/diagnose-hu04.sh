#!/usr/bin/env bash
# Diagnoses defect D-06 (HU04): tapping an artist in the list does not open its detail on some phones.
# Opens the app as a visitor, goes to Artistas, taps the artist and saves what happened
# (screens, view hierarchy, activity state and logcat) in e2e/profiling/reports/diag-hu04/.
# Usage: ./gradlew installDebug && e2e/profiling/diagnose-hu04.sh [artist-tag]   (default artist_item_100)
set -uo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
OUT="$HERE/reports/diag-hu04/$(date +%Y%m%d-%H%M%S)"
PKG="com.dev.wine.music.app"
TARGET="${1:-artist_item_100}"
mkdir -p "$OUT"
command -v adb >/dev/null || { echo "adb no está en el PATH"; exit 1; }

dump() { adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; adb shell cat /sdcard/ui.xml > "$OUT/$1.xml"; }
shot() { adb exec-out screencap -p > "$OUT/$1.png"; }
# Center of the node whose resource-id is $1 in the dump $2, as "x y".
center() {
  node -e '
    const xml = require("fs").readFileSync(process.argv[1], "utf8");
    const re = new RegExp("resource-id=\"" + process.argv[2] + "\"[^>]*bounds=\"\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]\"");
    const m = xml.match(re);
    if (!m) process.exit(1);
    console.log(Math.round((+m[1] + +m[3]) / 2), Math.round((+m[2] + +m[4]) / 2));
  ' "$OUT/$2.xml" "$1"
}
tap_id() {
  local id="$1" name="$2"
  dump "$name"
  local xy
  xy="$(center "$id" "$name")" || { echo "No encontré $id en pantalla (ver $OUT/$name.xml)"; shot "$name"; exit 1; }
  echo "Toco $id en $xy"
  adb shell input tap $xy
  sleep 3
}

# The local backend (BackVynils on this computer) is reached through adb reverse.
adb reverse tcp:3000 tcp:3000 >/dev/null
adb shell am force-stop "$PKG"
adb logcat -b all -c
adb shell am start -n "$PKG/.MainActivity" >/dev/null
sleep 4
tap_id role_visitor 01-rol
tap_id nav_artists 02-catalogo
dump 03-artistas
shot 03-artistas
grep -o "resource-id=\"$TARGET\"[^>]*" "$OUT/03-artistas.xml" > "$OUT/03-nodo-artista.txt"
tap_id "$TARGET" 03-artistas-antes-del-toque
shot 04-despues-del-toque
dump 04-despues-del-toque
adb shell dumpsys activity top | head -40 > "$OUT/04-activity-top.txt"
PID="$(adb shell pidof "$PKG" | tr -d '\r')"
adb logcat -d -v time > "$OUT/logcat-todo.txt"
[ -n "$PID" ] && adb logcat -d -v time --pid="$PID" > "$OUT/logcat-app.txt"
adb logcat -d -b crash > "$OUT/logcat-crash.txt"

if grep -q 'resource-id="artist_detail' "$OUT/04-despues-del-toque.xml"; then
  echo "RESULTADO: el detalle SÍ se abrió."
else
  echo "RESULTADO: el detalle NO se abrió."
fi
echo "Evidencia en $OUT"
