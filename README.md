# BRAVIA TV Launcher

A custom Android TV launcher for Sony BRAVIA and Google TV, with a tvOS-inspired five-app dock, Liquid Glass-inspired focus motion, and a StandBy-style clock screensaver. Navigate with a D-pad or phone remote. The interface uses Inter typography, icon-only controls, and a procedural wallpaper.

Built in Java for Android TV. Developed on a Sony BRAVIA VU3 running Android 14. The latest visual revision passed local tests and lint and is awaiting device verification. Other Android TV and Google TV models need compatibility testing.

## Build from source

Install JDK 21 and the Android SDK packages `platforms;android-34` and `build-tools;34.0.0`. Point `JAVA_HOME` and `ANDROID_HOME` at those installations, then run:

```sh
./build.sh
```

The build runs unit tests and Android lint and produces `launcher/app/build/outputs/apk/release/app-release.apk`. The first build downloads Gradle dependencies.

Open [the local design preview](design/preview.html) in a browser after cloning. It illustrates focus movement and the clock. See [design notes](design/DESIGN.md) for the Apple design references.

## Device setup

For the Sony VU3 setup, install Android platform-tools, create the Python environment below, and point `TV_HOST` at your authorized debugging connection. Set `TV_ADB_BINARY` to the path of your `adb` executable.

```sh
python3 -m venv tools/venv
tools/venv/bin/python -m pip install -r tools/requirements.txt
```

`install.py` installs the APK, applies the theme, and configures the Sony-specific Home redirect and clock screensaver. It grants the launcher's helper access to secure settings and enables its accessibility service. Review the script before using it on a device. Google TV's Home screen may appear briefly during handover.

The theme in `theme/config.json` controls dock order, visible apps, and focus settings. Device recovery snapshots are local to each installation.

## Contribute

Start with [the contribution guide](CONTRIBUTING.md). Documentation, offline tooling, and device compatibility reports are welcome.

See [LICENSE](LICENSE) for the source license. Icon and font notices are bundled with the project.
