# Optional YouTube ad filter

[YouTube-TV-NoAds](https://github.com/pearlxcore/YouTube-TV-NoAds/tree/89e72660afa74d45f5d863fa040419e6c903c06f)
builds a separate YouTube TV app. The filter removes known ad fields from
player and browse responses. The imported builder and patches use the [MIT license](LICENSE).

Use Python 3.12+, JDK 21, Apktool 3.0.3, and Android SDK Build Tools 36.
Supply the exact Google-signed YouTube Android TV 7.30.301 ARMv7 standalone APK.
The builder checks its SHA-256 before unpacking.

```sh
cd tools/youtube-noads
python3 -m venv .venv
.venv/bin/python -m pip install -r requirements.txt
.venv/bin/python patcher.py --base /path/to/original.apk --apktool /path/to/apktool.jar --build-tools /path/to/android-sdk/build-tools/36.0.0
```

Install `out/youtube-tv-noads-7.30.301.apk` on the TV. Keep `out/.signing/`
private and reuse its signing key for future builds.

To use the patched app from the existing dock slot, replace
`com.google.android.youtube.tv` with `local.x90h.youtube.tv.experimental` in
`dock_apps` and the YouTube `app_banners` entry in your local `theme/config.json`.
Reapply the theme from the repository root with `tools/theme.sh theme` using
the same ADB connection as Device setup. Restore both package names and reapply
the theme to switch back.

The upstream 7.30.301 release opened from the dock on a Sony BRAVIA VU3,
and the owner confirmed playback without ads in that test. The result
applies to that release and playback test. The patch filters known ad formats.

Stock YouTube can stay installed alongside the patched app. On the test TV,
the owner chose to remove stock YouTube from the current profile after
confirming playback. Removing stock YouTube is optional.
