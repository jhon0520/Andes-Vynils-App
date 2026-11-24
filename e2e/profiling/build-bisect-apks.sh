#!/usr/bin/env bash
# Builds the debug APK of several develop commits to find which change broke a tap on one phone.
# Each commit is built in its own worktree (next to the repo) with this repo's local.properties.
# Usage: e2e/profiling/build-bisect-apks.sh [commit ...]
#   default: a5a1605 (HU06, before the accessibility PRs), f7a042a (#62), fed7034 (#65), bc91609 (#67)
# Leaves e2e/profiling/apk/bisect/<commit>.apk. Install one with: adb install -r e2e/profiling/apk/bisect/<commit>.apk
set -euo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
REPO="$(cd "$HERE/../.." && pwd)"
COMMITS=("$@")
[ ${#COMMITS[@]} -gt 0 ] || COMMITS=(a5a1605 f7a042a fed7034 bc91609)
[ -f "$REPO/local.properties" ] || { echo "Falta local.properties en $REPO"; exit 1; }
mkdir -p "$HERE/apk/bisect"

for c in "${COMMITS[@]}"; do
  wt="$REPO/../wine-music-bisect-$c"
  echo "== $c: $(git -C "$REPO" log -1 --format=%s "$c")"
  [ -d "$wt" ] || git -C "$REPO" worktree add --detach "$wt" "$c" >/dev/null
  cp "$REPO/local.properties" "$wt/local.properties"
  (cd "$wt" && ./gradlew --quiet assembleDebug)
  cp "$wt/app/build/outputs/apk/debug/app-debug.apk" "$HERE/apk/bisect/$c.apk"
  echo "   listo: apk/bisect/$c.apk"
done
echo "Backend: $(grep '^api.base.url' "$REPO/local.properties" || echo 'el de gradle.properties')"
