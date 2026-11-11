#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
ADB="${TV_ADB_BINARY:-$ROOT/.local/android-sdk/platform-tools/adb}"
TV="${TV_HOST:?Set TV_HOST to the TV current wireless debugging IP:port}"
"$ADB" connect "$TV"
"$ADB" -s "$TV" shell pm enable --user 0 com.google.android.apps.tv.launcherx
"$ADB" -s "$TV" shell 'settings delete secure enabled_accessibility_services'
"$ADB" -s "$TV" shell 'settings put secure accessibility_enabled 0'
"$ADB" -s "$TV" shell cmd package set-home-activity --user 0 com.google.android.apps.tv.launcherx/.home.HomeActivity
"$ADB" -s "$TV" shell pm disable-user --user 0 com.gathin.tvhome
"$ADB" -s "$TV" shell input keyevent KEYCODE_HOME
echo "Restored Google TV Home and previous accessibility settings."
