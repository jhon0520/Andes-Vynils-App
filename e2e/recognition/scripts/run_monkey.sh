#!/usr/bin/env bash
# Random recognition run (REC-01 / REC-02) with fixed seeds.
# Usage: run_monkey.sh <serial> <device-label> <events> <seed>... 
# Env: WIFI_OFF_AFTER=<seconds>  -> disables wifi that many seconds into each run (REC-02).
#      With BACKEND_REVERSE=1 the app reaches BackVynils through `adb reverse tcp:3000`; the tunnel is
#      removed at the same moment, so the backend becomes unreachable together with the internet.
set -u
SERIAL=$1; LABEL=$2; EVENTS=$3; shift 3
PKG=com.dev.wine.music.app
OUT=$(cd "$(dirname "$0")/.." && pwd)/results/random
mkdir -p "$OUT"
ADB="adb -s $SERIAL"
for SEED in "$@"; do
  NAME="monkey-$LABEL-seed$SEED"
  $ADB shell svc wifi enable
  [ "${BACKEND_REVERSE:-0}" = "1" ] && $ADB reverse tcp:3000 tcp:3000 >/dev/null
  rm -f "$OUT/$NAME-network.txt"
  $ADB shell am force-stop $PKG
  $ADB logcat -c
  # Keep the notification shade closed so monkey cannot change system settings on a physical phone.
  ( while true; do $ADB shell cmd statusbar collapse >/dev/null 2>&1 || $ADB shell service call statusbar 2 >/dev/null 2>&1; sleep 1; done ) &
  GUARD=$!
  if [ -n "${WIFI_OFF_AFTER:-}" ]; then
    ( sleep "$WIFI_OFF_AFTER"; echo "$(date +%T) wifi disabled" >> "$OUT/$NAME-network.txt"; $ADB shell svc wifi disable
      [ "${BACKEND_REVERSE:-0}" = "1" ] && $ADB reverse --remove tcp:3000 ) &
    NET=$!
  fi
  START=$(date +%T)
  $ADB shell monkey -p $PKG -s "$SEED" --throttle 250 --pct-syskeys 0 --pct-appswitch 5 \
      --ignore-security-exceptions -v -v "$EVENTS" > "$OUT/$NAME.txt" 2>&1
  END=$(date +%T)
  kill $GUARD 2>/dev/null; [ -n "${NET:-}" ] && kill $NET 2>/dev/null
  $ADB exec-out screencap -p > "$OUT/$NAME-last-screen.png"
  $ADB logcat -d -b crash > "$OUT/$NAME-crash.txt"
  # HTTP results (OkHttp logs at BODY level in debug): shows how the app handled the network cut.
  $ADB logcat -d | grep -E "HTTP FAILED|okhttp.*<-- [0-9]{3}" > "$OUT/$NAME-http.txt" || true
  $ADB shell svc wifi enable
  [ "${BACKEND_REVERSE:-0}" = "1" ] && $ADB reverse tcp:3000 tcp:3000 >/dev/null
  echo "$NAME start=$START end=$END $(tail -1 "$OUT/$NAME.txt") crash_lines=$(wc -l < "$OUT/$NAME-crash.txt")"
done
