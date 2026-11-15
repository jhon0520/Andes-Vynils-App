#!/usr/bin/env bash
# Runs the whole profiling series on one device without touching it: cold start (3 runs) and
# every HU walkthrough 3 times with perf-capture.sh --auto. Then prints the median of each metric.
#
# Usage:
#   ./scripts/perf-run-all.sh <device-label> [-s SERIAL]
#
# Install the profiling build first (./gradlew installProfiling) and keep the backend up.
set -euo pipefail

if [ $# -lt 1 ]; then
  sed -n '2,9p' "$0" | sed 's/^# \{0,1\}//'
  exit 1
fi

DEVICE_LABEL="$1"
shift
APP_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="${APP_DIR}/docs/performance/results/${DEVICE_LABEL}"
STORIES=(HU01 HU02 HU03 HU04 HU05)
REPEATS=3

"${APP_DIR}/scripts/perf-capture.sh" "$DEVICE_LABEL" startup "$@"
for story in "${STORIES[@]}"; do
  for run in $(seq 1 "$REPEATS"); do
    echo "== ${story} run ${run}/${REPEATS}"
    "${APP_DIR}/scripts/perf-capture.sh" "$DEVICE_LABEL" "$story" --auto "$@" > /dev/null
  done
done

# Median of the last $REPEATS summaries of each story.
median() { sort -n | awk '{ v[NR] = $1 } END { if (NR) print v[int((NR + 1) / 2)]; else print "n/a" }'; }
field() { awk -F': *' -v k="$1" '$1 == k { print $2 }' "$2"; }

REPORT="${OUT_DIR}/medians-$(date +%Y%m%d-%H%M%S).md"
{
  echo "| HU | Janky frames (%) | Frame p90 (ms) | PSS (MB) | Java heap (MB) | CPU (%) | ANR |"
  echo "|---|---|---|---|---|---|---|"
  for story in "${STORIES[@]}"; do
    files=$(ls -t "${OUT_DIR}/${story}"-*-summary.txt | head -n "$REPEATS")
    jank=$(for f in $files; do field "janky frames" "$f" | sed -E 's/.*\(([0-9.]+)%\).*/\1/'; done | median)
    p90=$(for f in $files; do field "frame p90 / p95" "$f" | sed -E 's/^([0-9]+)ms.*/\1/'; done | median)
    pss=$(for f in $files; do field "total PSS" "$f" | awk '{ printf "%.1f\n", $1 / 1024 }'; done | median)
    heap=$(for f in $files; do field "java heap" "$f" | awk '{ printf "%.1f\n", $1 / 1024 }'; done | median)
    cpu=$(for f in $files; do field "cpu (avg, all cores)" "$f" | awk '{ print $1 }'; done | median)
    anr=$(for f in $files; do field "ANR" "$f"; done | awk '{ s += $1 } END { print s }')
    echo "| ${story} | ${jank} | ${p90} | ${pss} | ${heap} | ${cpu} | ${anr} |"
  done
  echo
  echo "Cold start: $(tail -1 "$(ls -t "${OUT_DIR}"/startup-*.txt | head -1)")"
} | tee "$REPORT"
echo
echo "Report: ${REPORT}"
