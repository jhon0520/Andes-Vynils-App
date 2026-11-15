#!/usr/bin/env bash
# Captures the performance metrics of one user story (HU) on one device, the same way on every
# device of the team, for the sprint 2 profiling report (docs/performance/README.md).
#
# Usage:
#   ./scripts/perf-capture.sh <device-label> startup        # cold start time, 3 runs
#   ./scripts/perf-capture.sh <device-label> <HU>           # frames, memory and CPU of a walkthrough
#   ./scripts/perf-capture.sh <device-label> <HU> -s SERIAL  # pick a device when several are connected
#   ./scripts/perf-capture.sh <device-label> <HU> --auto     # restart the app and run the walkthrough
#                                                            # with scripts/perf-walkthrough.sh
#
# Examples:
#   ./scripts/perf-capture.sh samsung-a10 startup
#   ./scripts/perf-capture.sh samsung-a10 HU01
#
# Install the profiling build first: ./gradlew installProfiling
# Results go to docs/performance/results/<device-label>/.
set -euo pipefail

PACKAGE="com.dev.wine.music.app"
ACTIVITY="${PACKAGE}/.MainActivity"
STARTUP_RUNS=3

if [ $# -lt 2 ]; then
  sed -n '2,17p' "$0" | sed 's/^# \{0,1\}//'
  exit 1
fi

DEVICE_LABEL="$1"
STORY="$2"
shift 2
ADB_ARGS=()
AUTO=false
while [ $# -gt 0 ]; do
  case "$1" in
    -s) ADB_ARGS=(-s "$2"); shift 2 ;;
    --auto) AUTO=true; shift ;;
    *) echo "Unknown option: $1" >&2; exit 1 ;;
  esac
done

adb_cmd() { adb ${ADB_ARGS[@]+"${ADB_ARGS[@]}"} "$@"; }
shell() { adb_cmd shell "$@" | tr -d '\r'; }

APP_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="${APP_DIR}/docs/performance/results/${DEVICE_LABEL}"
STAMP="$(date +%Y%m%d-%H%M%S)"
mkdir -p "$OUT_DIR"

if ! shell pm list packages "$PACKAGE" | grep -q "^package:${PACKAGE}$"; then
  echo "The app is not installed on the device. Run ./gradlew installProfiling first." >&2
  exit 1
fi

# Device sheet, written once per device: the report needs model, Android version and RAM.
DEVICE_FILE="${OUT_DIR}/device.txt"
if [ ! -f "$DEVICE_FILE" ]; then
  {
    echo "label:        ${DEVICE_LABEL}"
    echo "manufacturer: $(shell getprop ro.product.manufacturer)"
    echo "model:        $(shell getprop ro.product.model)"
    echo "android:      $(shell getprop ro.build.version.release) (API $(shell getprop ro.build.version.sdk))"
    echo "cpu abi:      $(shell getprop ro.product.cpu.abi)"
    echo "total ram:    $(shell cat /proc/meminfo | awk '/MemTotal/ {printf "%.1f GB", $2 / 1048576}')"
    echo "debuggable:   $(shell dumpsys package "$PACKAGE" | grep -q 'DEBUGGABLE' && echo yes || echo no)"
    echo "captured:     $(date '+%Y-%m-%d %H:%M')"
  } > "$DEVICE_FILE"
  echo "Device sheet: ${DEVICE_FILE}"
fi

if [ "$STORY" = "startup" ]; then
  FILE="${OUT_DIR}/startup-${STAMP}.txt"
  times=()
  for run in $(seq 1 "$STARTUP_RUNS"); do
    shell am force-stop "$PACKAGE"
    sleep 2
    total="$(shell am start -W -S -n "$ACTIVITY" | awk -F': ' '/TotalTime/ {print $2}')"
    echo "run ${run}: ${total} ms" | tee -a "$FILE"
    times+=("$total")
    sleep 3
  done
  median="$(printf '%s\n' "${times[@]}" | sort -n | sed -n "$(( (STARTUP_RUNS + 1) / 2 ))p")"
  echo "median cold start (TotalTime): ${median} ms" | tee -a "$FILE"
  exit 0
fi

FILE_PREFIX="${OUT_DIR}/${STORY}-${STAMP}"

if [ "$AUTO" = true ]; then
  # Same starting point every run: cold process on the profile selection screen.
  shell am force-stop "$PACKAGE"
  sleep 2
  shell am start -W -n "$ACTIVITY" > /dev/null
  sleep 3
fi

# Frame stats count from here on, so they only cover the walkthrough.
shell dumpsys gfxinfo "$PACKAGE" reset > /dev/null
pid="$(shell pidof "$PACKAGE" || true)"
if [ -z "$pid" ]; then
  echo "Open the app on the device first (it must be running)." >&2
  exit 1
fi
cpu_start="$(shell cat "/proc/${pid}/stat" | awk '{print $14 + $15}')"
uptime_start="$(shell cat /proc/uptime | awk '{print $1}')"

if [ "$AUTO" = true ]; then
  "${APP_DIR}/scripts/perf-walkthrough.sh" "$STORY" ${ADB_ARGS[@]+"${ADB_ARGS[@]}"}
else
  echo
  echo "Do the ${STORY} walkthrough on the device now (docs/performance/README.md, table «Recorridos»)."
  read -r -p "Press Enter when you finish... " _
fi

cpu_end="$(shell cat "/proc/${pid}/stat" | awk '{print $14 + $15}')"
uptime_end="$(shell cat /proc/uptime | awk '{print $1}')"
cores="$(shell nproc 2>/dev/null || echo 1)"
clk_tck=100

shell dumpsys gfxinfo "$PACKAGE" > "${FILE_PREFIX}-gfxinfo.txt"
shell dumpsys meminfo "$PACKAGE" > "${FILE_PREFIX}-meminfo.txt"

frames="$(awk -F': ' '/^Total frames rendered/ {print $2; exit}' "${FILE_PREFIX}-gfxinfo.txt")"
janky="$(awk -F': ' '/^Janky frames/ {print $2; exit}' "${FILE_PREFIX}-gfxinfo.txt")"
p90="$(awk -F': ' '/^90th percentile/ {print $2; exit}' "${FILE_PREFIX}-gfxinfo.txt")"
p95="$(awk -F': ' '/^95th percentile/ {print $2; exit}' "${FILE_PREFIX}-gfxinfo.txt")"
pss="$(awk '/TOTAL PSS:/ {print $3; exit} /^ *TOTAL / {print $2; exit}' "${FILE_PREFIX}-meminfo.txt")"
java_heap="$(awk -F': *' '/Java Heap:/ {print $2; exit}' "${FILE_PREFIX}-meminfo.txt" | awk '{print $1}')"
native_heap="$(awk -F': *' '/Native Heap:/ {print $2; exit}' "${FILE_PREFIX}-meminfo.txt" | awk '{print $1}')"
graphics="$(awk -F': *' '/Graphics:/ {print $2; exit}' "${FILE_PREFIX}-meminfo.txt" | awk '{print $1}')"
cpu_pct="$(awk -v a="$cpu_start" -v b="$cpu_end" -v t0="$uptime_start" -v t1="$uptime_end" -v hz="$clk_tck" -v n="$cores" \
  'BEGIN { s = t1 - t0; if (s <= 0) { print "n/a"; exit } printf "%.1f", ((b - a) / hz) / (s * n) * 100 }')"
anrs="$(shell dumpsys activity processes "$PACKAGE" | grep -c 'notResponding=true' || true)"

SUMMARY="${FILE_PREFIX}-summary.txt"
{
  echo "story:            ${STORY}"
  echo "device:           ${DEVICE_LABEL}"
  echo "frames rendered:  ${frames:-n/a}"
  echo "janky frames:     ${janky:-n/a}"
  echo "frame p90 / p95:  ${p90:-n/a} / ${p95:-n/a}"
  echo "total PSS:        ${pss:-n/a} KB"
  echo "java heap:        ${java_heap:-n/a} KB"
  echo "native heap:      ${native_heap:-n/a} KB"
  echo "graphics:         ${graphics:-n/a} KB"
  echo "cpu (avg, all cores): ${cpu_pct} %"
  echo "ANR:              ${anrs}"
} | tee "$SUMMARY"
echo
echo "Raw dumps: ${FILE_PREFIX}-gfxinfo.txt, ${FILE_PREFIX}-meminfo.txt"
