#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
PYTHON="${SONY_PYTHON:-python3}"
"$PYTHON" -m venv "$ROOT/tools/venv"
if [ -d "$ROOT/.local/wheels" ]; then
  "$ROOT/tools/venv/bin/python" -m pip install --no-index --find-links "$ROOT/.local/wheels" -r "$ROOT/requirements-lock.txt"
else
  "$ROOT/tools/venv/bin/python" -m pip install -r "$ROOT/tools/requirements.txt"
fi
echo "Python tools ready."
