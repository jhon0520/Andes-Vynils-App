#!/usr/bin/env bash
# Runs the profiling scenarios on the one phone connected by USB and writes results/<build>/<model>/.
# Usage: e2e/profiling/run-profiling.sh <antes|despues> [runs]   (default 3 runs)
# Needs: adb on the PATH, the APK from build-apks.sh and the backend up (BackVynils on localhost:3000).
set -uo pipefail

HERE="$(cd "$(dirname "$0")" && pwd)"
KRAKEN_DIR="$HERE/../kraken"
BUILD="${1:?Indica la versión: antes o despues}"
RUNS="${2:-3}"
APK="$HERE/apk/$BUILD.apk"

[ -f "$APK" ] || { echo "No existe $APK. Ejecuta primero e2e/profiling/build-apks.sh"; exit 1; }
command -v adb >/dev/null || { echo "adb no está en el PATH: export PATH=\"\$HOME/Library/Android/sdk/platform-tools:\$PATH\""; exit 1; }
DEVICES="$(adb devices | awk 'NR > 1 && $2 == "device" { print $1 }')"
[ "$(printf '%s\n' "$DEVICES" | grep -c .)" = "1" ] || { echo "Conecta exactamente un teléfono por USB:"; adb devices; exit 1; }
export ANDROID_SERIAL="$DEVICES"
[ -d "$KRAKEN_DIR/node_modules" ] || (cd "$KRAKEN_DIR" && npm install)
# Cucumber checks that it is installed where it runs (here): reuse the Kraken install instead of a second one.
ln -sfn ../kraken/node_modules "$HERE/node_modules"
export PATH="$KRAKEN_DIR/node_modules/.bin:$PATH"

cat > "$HERE/mobile.json" <<EOF
{
  "type": "singular",
  "apk_path": "$APK",
  "apk_package": "com.dev.wine.music.app",
  "apk_launch_activity": "com.dev.wine.music.app.MainActivity"
}
EOF

# Same conditions in every run: animations at their normal speed and the screen always on.
# The original values are put back at the end.
SETTINGS="window_animation_scale transition_animation_scale animator_duration_scale stay_on_while_plugged_in"
ORIGINAL=""
for s in $SETTINGS; do ORIGINAL="$ORIGINAL $s=$(adb shell settings get global "$s" | tr -d '\r')"; done
restore() {
  for pair in $ORIGINAL; do
    value="${pair#*=}"
    if [ "$value" = "null" ]; then adb shell settings delete global "${pair%%=*}" >/dev/null; else adb shell settings put global "${pair%%=*}" "$value"; fi
  done
}
trap restore EXIT
for s in window_animation_scale transition_animation_scale animator_duration_scale; do adb shell settings put global "$s" 1; done
adb shell settings put global stay_on_while_plugged_in 7

# Kraken stops the whole queue as soon as one scenario fails, so each feature runs on its own
# Kraken (in .runs/<scenario>, linked to the real files): a failure only loses that scenario.
# PROFILE_FEATURES="P-HU01 P-HU04" runs only some of them.
FEATURES="${PROFILE_FEATURES:-$(cd "$HERE/features" && ls *.feature | sed 's/\.feature$//')}"
FAILED_LIST=""
for run in $(seq 1 "$RUNS"); do
  for name in $FEATURES; do
    echo "== Corrida $run de $RUNS · $name · $BUILD · $(adb shell getprop ro.product.model | tr -d '\r') =="
    dir="$HERE/.runs/$name"
    mkdir -p "$dir/features"
    ln -sfn "$HERE/features/$name.feature" "$dir/features/$name.feature"
    ln -sfn "$HERE/features/mobile" "$dir/features/mobile"
    for f in mobile.json properties.json node_modules; do ln -sfn "$HERE/$f" "$dir/$f"; done
    (cd "$dir" && PROFILE_BUILD="$BUILD" PROFILE_RUN="$run" kraken-node run) || FAILED_LIST="$FAILED_LIST $name(corrida $run)"
    # Nothing of a scenario may stay alive into the next one.
    pkill -f "kraken-node/bin/cucumber" 2>/dev/null || true
  done
done

node "$HERE/summarize.js"
if [ -n "$FAILED_LIST" ]; then
  echo "Fallaron:$FAILED_LIST. El log del teléfono de cada falla está en e2e/profiling/reports/fallas/"
  exit 1
fi
