#!/usr/bin/env python3
"""Install this personal Sony build. Never disable a stock/system package."""
import os, pathlib, shlex, subprocess
ROOT = pathlib.Path(__file__).resolve().parent
ADB = os.environ.get("TV_ADB_BINARY", str(ROOT / ".local/android-sdk/platform-tools/adb"))
HOST = os.environ.get("TV_HOST")
if not HOST:
    raise SystemExit("Set TV_HOST to the TV's current debugging IP:port.")
def run(*args):
    return subprocess.run([ADB, "-s", HOST, *args], capture_output=True, text=True, check=True).stdout
subprocess.run([ADB, "connect", HOST], check=True)
run("push", str(ROOT / "launcher/app/build/outputs/apk/release/app-release.apk"), "/data/local/tmp/sony-tv-custom.apk")
result = run("shell", "pm install -r /data/local/tmp/sony-tv-custom.apk")
print(result)
if "Success" not in result:
    raise SystemExit("Installation failed; existing Home settings were left alone.")
run("shell", "pm grant com.gathin.tvhome android.permission.WRITE_SECURE_SETTINGS")
services = run("shell", "settings get secure enabled_accessibility_services").strip()
services = [] if services in ("", "null") else services.split(":")
helper = "com.gathin.tvhome/.HomeKeyService"
if helper not in services: services.append(helper)
run("shell", "settings put secure enabled_accessibility_services ''")
run("shell", "settings put secure enabled_accessibility_services " + shlex.quote(":".join(services)))
run("shell", "settings put secure accessibility_enabled 1")
run("shell", "cmd package set-home-activity --user 0 com.google.android.apps.tv.launcherx/.home.HomeActivity")
run("shell", "settings put secure screensaver_components com.gathin.tvhome/.ClockDream")
run("shell", "settings put secure screensaver_enabled 1")
env = dict(os.environ, TV_ADB_BINARY=ADB, TV_HOST=HOST)
subprocess.run([str(ROOT / "tools/theme.sh"), str(ROOT / "theme")], env=env, check=True)
print(run("shell", "am start -W -n com.gathin.tvhome/.MainActivity"))
