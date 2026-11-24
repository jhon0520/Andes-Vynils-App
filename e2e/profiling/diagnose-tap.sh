#!/usr/bin/env bash
# Diagnoses a tap that does not open its screen during profiling (P-HU06 to P-HU08 on the HONOR LGN-NX3).
# Leave the app on the screen that has the element, then run:
#   e2e/profiling/diagnose-tap.sh <element-tag> <tag-expected-after-the-tap>
#   e.g. e2e/profiling/diagnose-tap.sh create_album_button album_create_form
# Taps the center of the element with `adb shell input tap` (as the profiling does) and saves the view
# hierarchy, screenshots and logcat in e2e/profiling/reports/diag-tap/<tag>-<time>/.
set -uo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
TAG="${1:?Indica el tag del elemento a tocar}"
TARGET="${2:?Indica el tag que debe aparecer después del toque}"
OUT="$HERE/reports/diag-tap/$TAG-$(date +%H%M%S)"
PKG="com.dev.wine.music.app"
mkdir -p "$OUT"
command -v adb >/dev/null || { echo "adb no está en el PATH"; exit 1; }

dump() { adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; adb shell cat /sdcard/ui.xml > "$OUT/$1.xml"; }
shot() { adb exec-out screencap -p > "$OUT/$1.png"; }

adb logcat -c
dump 01-antes
shot 01-antes
NODE="$(grep -o "<node [^>]*resource-id=\"$TAG\"[^>]*>" "$OUT/01-antes.xml" | head -1)"
[ -n "$NODE" ] || { echo "No encontré $TAG en pantalla (ver $OUT/01-antes.xml)"; exit 1; }
echo "$NODE" > "$OUT/nodo.txt"
BOUNDS="$(echo "$NODE" | sed -E 's/.*bounds="\[([0-9]+),([0-9]+)\]\[([0-9]+),([0-9]+)\]".*/\1 \2 \3 \4/')"
read -r X1 Y1 X2 Y2 <<< "$BOUNDS"
X=$(( (X1 + X2) / 2 )); Y=$(( (Y1 + Y2) / 2 ))
echo "Nodo: $(echo "$NODE" | grep -oE '(clickable|enabled|focusable)="[a-z]+"' | tr '\n' ' ')"
echo "Límites: [$X1,$Y1][$X2,$Y2] · toco en $X $Y"
adb shell input tap "$X" "$Y"
sleep 3
dump 02-despues
shot 02-despues
adb shell dumpsys activity top | head -30 > "$OUT/activity-top.txt"
adb shell dumpsys input | grep -iE "touch|pointer|gesture|hitouch" | head -40 > "$OUT/input.txt"
PID="$(adb shell pidof "$PKG" | tr -d '\r')"
adb logcat -d -v time > "$OUT/logcat-todo.txt"
[ -n "$PID" ] && adb logcat -d -v time --pid="$PID" > "$OUT/logcat-app.txt"

if grep -q "resource-id=\"$TARGET\"" "$OUT/02-despues.xml"; then
  echo "RESULTADO: $TARGET SÍ apareció."
else
  echo "RESULTADO: $TARGET NO apareció."
fi
echo "Evidencia en $OUT"
