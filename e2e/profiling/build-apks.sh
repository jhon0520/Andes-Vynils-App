#!/usr/bin/env bash
# Builds the two APKs to compare, with the same local.properties (same backend):
#   apk/despues.apk  the branch checked out now (with the optimizations)
#   apk/antes.apk    develop before the optimizations, built in a separate worktree
# Usage: e2e/profiling/build-apks.sh [before-commit]   (default 8921089)
set -euo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
REPO="$(cd "$HERE/../.." && pwd)"
BEFORE="${1:-8921089}"
WORKTREE="$REPO/../wine-music-antes-$BEFORE"

[ -f "$REPO/local.properties" ] || { echo "Falta local.properties (sdk.dir y api.base.url) en $REPO"; exit 1; }
mkdir -p "$HERE/apk"

echo "== APK despues: $(git -C "$REPO" rev-parse --abbrev-ref HEAD) @ $(git -C "$REPO" rev-parse --short HEAD) =="
(cd "$REPO" && ./gradlew --quiet assembleDebug)
cp "$REPO/app/build/outputs/apk/debug/app-debug.apk" "$HERE/apk/despues.apk"

echo "== APK antes: $BEFORE (worktree en $WORKTREE) =="
[ -d "$WORKTREE" ] || git -C "$REPO" worktree add --detach "$WORKTREE" "$BEFORE"
cp "$REPO/local.properties" "$WORKTREE/local.properties"
(cd "$WORKTREE" && ./gradlew --quiet assembleDebug)
cp "$WORKTREE/app/build/outputs/apk/debug/app-debug.apk" "$HERE/apk/antes.apk"

echo "Listo: $HERE/apk/antes.apk y $HERE/apk/despues.apk"
echo "Backend usado: $(grep '^api.base.url' "$REPO/local.properties" || echo 'el de gradle.properties')"
