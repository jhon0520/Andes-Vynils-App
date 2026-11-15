#!/usr/bin/env bash
# Drives the profiling walkthrough of one user story (HU) with adb, so every run on every device
# is identical (docs/performance/README.md, table «Recorridos»). Elements are found by testTag:
# MainScreen sets testTagsAsResourceId, so uiautomator sees each testTag as a resource-id.
#
# Usage:
#   ./scripts/perf-walkthrough.sh <HU> [-s SERIAL]      # HU01 | HU02 | HU03 | HU04 | HU05
#
# The app must be open on the profile selection screen (perf-capture.sh --auto does this).
# Note: each uiautomator dump costs ~0.5–1 s of device CPU; it is part of every run, so runs stay
# comparable, but CPU figures include it.
set -euo pipefail

STORY="${1:-}"
shift || true
ADB_ARGS=()
if [ "${1:-}" = "-s" ] && [ -n "${2:-}" ]; then
  ADB_ARGS=(-s "$2")
fi

adb_cmd() { adb ${ADB_ARGS[@]+"${ADB_ARGS[@]}"} "$@"; }
shell() { adb_cmd shell "$@" | tr -d '\r'; }

UI_XML="$(mktemp -t vinilos-ui).xml"
trap 'rm -f "$UI_XML"' EXIT

# Writes the current screen hierarchy to $UI_XML (retries: dumps fail while the UI is animating).
dump() {
  local attempt
  for attempt in 1 2 3; do
    if shell uiautomator dump /sdcard/vinilos-ui.xml 2>/dev/null | grep -q 'dumped'; then
      adb_cmd pull /sdcard/vinilos-ui.xml "$UI_XML" > /dev/null 2>&1 && return 0
    fi
    sleep 1
  done
  echo "uiautomator dump failed" >&2
  return 1
}

# Prints "x y" of the center of the node whose resource-id is exactly $1 (empty if missing).
center_of() {
  grep -o "resource-id=\"$1\"[^>]*bounds=\"[^\"]*\"" "$UI_XML" | head -1 |
    sed -E 's/.*bounds="\[([0-9]+),([0-9]+)\]\[([0-9]+),([0-9]+)\]".*/\1 \2 \3 \4/' |
    awk '{ print int(($1 + $3) / 2), int(($2 + $4) / 2) }'
}

# Prints the resource-ids that start with $1, in screen order.
ids_with_prefix() {
  grep -o "resource-id=\"$1[^\"]*\"" "$UI_XML" | sed -E 's/resource-id="([^"]*)"/\1/' | awk '!seen[$0]++'
}

# Waits up to $2 seconds (default 20) until the testTag $1 is on screen.
wait_for() {
  local tag="$1" timeout="${2:-20}" start
  start=$(date +%s)
  while true; do
    dump
    if grep -q "resource-id=\"${tag}\"" "$UI_XML"; then return 0; fi
    if [ $(( $(date +%s) - start )) -ge "$timeout" ]; then
      echo "Timeout waiting for ${tag}" >&2
      return 1
    fi
    sleep 1
  done
}

tap() {
  local tag="$1" xy
  wait_for "$tag"
  xy="$(center_of "$tag")"
  # shellcheck disable=SC2086
  shell input tap $xy
  sleep 1
}

back() { shell input keyevent KEYCODE_BACK; sleep 1; }

# Scrolls the list with testTag $1 down $2 times and back up the same number of times.
scroll_list() {
  local list="$1" times="${2:-4}" bounds x top bottom i
  wait_for "$list"
  bounds="$(grep -o "resource-id=\"${list}\"[^>]*bounds=\"[^\"]*\"" "$UI_XML" | head -1 |
    sed -E 's/.*bounds="\[([0-9]+),([0-9]+)\]\[([0-9]+),([0-9]+)\]".*/\1 \2 \3 \4/')"
  read -r x1 top x2 bottom <<< "$bounds"
  x=$(( (x1 + x2) / 2 ))
  for i in $(seq 1 "$times"); do shell input swipe "$x" $(( bottom - 100 )) "$x" $(( top + 100 )) 300; done
  for i in $(seq 1 "$times"); do shell input swipe "$x" $(( top + 100 )) "$x" $(( bottom - 100 )) 300; done
  sleep 1
}

# Opens the first $2 items whose testTag starts with $1, going back to the list after each one,
# and waits for the detail testTag $3 every time. Then opens the first one again (cache hit).
open_items() {
  local prefix="$1" count="$2" detail="$3" list="$4" items item
  wait_for "$list"
  items="$(ids_with_prefix "$prefix" | head -n "$count")"
  if [ -z "$items" ]; then echo "No ${prefix}* items on screen" >&2; return 1; fi
  for item in $items $(echo "$items" | head -1); do
    tap "$item"
    wait_for "$detail"
    sleep 2
    back
    wait_for "$list"
  done
}

enter_as_visitor() {
  tap role_visitor
  wait_for album_list 30
}

case "$STORY" in
  HU01)
    enter_as_visitor
    scroll_list album_list 5
    tap album_search_input
    shell input text a
    sleep 2
    tap album_search_clear_button
    tap album_genre_tab_salsa
    sleep 2
    tap album_genre_tab_all
    sleep 2
    ;;
  HU02)
    enter_as_visitor
    open_items album_item_ 3 album_detail album_list
    ;;
  HU03)
    enter_as_visitor
    tap nav_artists
    scroll_list artist_list 3
    tap artist_type_tab_musician
    sleep 2
    tap artist_type_tab_band
    sleep 2
    tap artist_type_tab_all
    sleep 1
    tap nav_albums
    tap nav_artists
    wait_for artist_list
    ;;
  HU04)
    enter_as_visitor
    tap nav_artists
    open_items artist_item_ 3 artist_detail artist_list
    ;;
  HU05)
    enter_as_visitor
    tap nav_collectors
    wait_for nav_collectors
    sleep 3
    dump
    # The collector list has no testTag: scroll the middle of the screen.
    read -r w h <<< "$(shell wm size | awk -F'[ x]' '/size/ {print $(NF-1), $NF}' | tail -1)"
    x=$(( w / 2 )); low=$(( h * 7 / 10 )); high=$(( h * 3 / 10 ))
    for _ in 1 2; do shell input swipe "$x" "$low" "$x" "$high" 300; done
    for _ in 1 2; do shell input swipe "$x" "$high" "$x" "$low" 300; done
    tap nav_albums
    tap nav_collectors
    sleep 2
    ;;
  *)
    sed -n '2,12p' "$0" | sed 's/^# \{0,1\}//'
    exit 1
    ;;
esac

echo "Walkthrough ${STORY} done."
