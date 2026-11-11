#!/usr/bin/env python3
"""Re-enable the entertainment apps disabled in the original, locally backed-up session."""
import json, os, pathlib, subprocess
ROOT = pathlib.Path(__file__).resolve().parent
HOST = os.environ.get("TV_HOST")
if not HOST: raise SystemExit("Set TV_HOST to the TV's current debugging IP:port.")
ADB = os.environ.get("TV_ADB_BINARY", str(ROOT / ".local/android-sdk/platform-tools/adb"))
record = json.loads((ROOT / "backups/original-tv/unwanted-apps-recovery.json").read_text())
requested = [line.split("#", 1)[0].strip() for line in (ROOT / "backups/unwanted-apps.txt").read_text().splitlines()]
subprocess.run([ADB, "connect", HOST], check=True)
for package in filter(None, requested):
    if record["packages"].get(package) == "enabled":
        subprocess.run([ADB, "-s", HOST, "shell", "pm", "enable", "--user", "0", package], check=True)
print("Previously enabled entertainment apps restored; Home and system services left alone.")
