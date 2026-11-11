#!/usr/bin/env python3
"""Return to the prior launcher build/theme and saved screensaver settings."""
import os, pathlib, shlex, subprocess, json
ROOT = pathlib.Path(__file__).resolve().parent
ADB = os.environ.get("TV_ADB_BINARY", str(ROOT / ".local/android-sdk/platform-tools/adb"))
HOST = os.environ.get("TV_HOST")
if not HOST: raise SystemExit("Set TV_HOST to the TV's current debugging IP:port.")
def run(*args):
    return subprocess.run([ADB, "-s", HOST, *args], capture_output=True, text=True, check=True).stdout
subprocess.run([ADB, "connect", HOST], check=True)
run("push", str(ROOT / "backups/previous-launcher.apk"), "/data/local/tmp/sony-tv-previous.apk")
result = run("shell", "pm install -r /data/local/tmp/sony-tv-previous.apk")
print(result)
if "Success" not in result: raise SystemExit("Rollback install failed; settings left alone.")
state = json.loads((ROOT / "backups/tv-settings-before-custom.json").read_text())
for key, value in state.items():
    command = "settings delete secure " + key if value == "null" else "settings put secure " + key + " " + shlex.quote(value)
    run("shell", command)
env = dict(os.environ, TV_ADB_BINARY=ADB, TV_HOST=HOST)
subprocess.run([str(ROOT / "tools/theme.sh"), str(ROOT / "backups/previous-theme")], env=env, check=True)
print(run("shell", "am start -W -n com.gathin.tvhome/.MainActivity"))
