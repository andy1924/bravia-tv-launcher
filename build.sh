#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
if [ -z "${JAVA_HOME:-}" ] && [ -d /opt/homebrew/opt/openjdk@21 ]; then
  export JAVA_HOME=/opt/homebrew/opt/openjdk@21
fi
export ANDROID_HOME="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$ROOT/.local/android-sdk}}"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export GRADLE_USER_HOME="${GRADLE_USER_HOME:-$ROOT/.local/gradle}"
if [ ! -d "$ANDROID_HOME/platforms/android-34" ]; then
  echo "Set ANDROID_HOME to an SDK with Android platform 34 installed." >&2
  exit 1
fi
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > "$ROOT/launcher/local.properties"
cd "$ROOT/launcher"
./gradlew --no-daemon testReleaseUnitTest lintRelease assembleRelease --console=plain "$@"
