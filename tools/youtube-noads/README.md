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

## Troubleshooting and rollback

The commands below were reviewed against the repository's scripts and have
**not been verified on a device**. Run them with the same ADB connection as
Device setup (`TV_HOST` set, and `TV_ADB_BINARY` if needed).

### Package names

| App | Package name |
| --- | --- |
| Stock YouTube TV | `com.google.android.youtube.tv` |
| Patched YouTube TV | `local.x90h.youtube.tv.experimental` |

### Check which apps are installed

```sh
tools/tv.py 'pm list packages --user 0'
```

Look for the two package names above in the output. If the patched name is
missing, the patched APK is not installed for the current profile. Install
`out/youtube-tv-noads-7.30.301.apk` again. If only the stock name is missing,
see "Restore stock YouTube" below.

### The patched app is installed but the dock opens stock YouTube

The dock follows `theme/config.json`. If `dock_apps` and the YouTube
`app_banners` entry still name `com.google.android.youtube.tv`, the tile opens
stock YouTube even though the patched app is installed. Replace both with
`local.x90h.youtube.tv.experimental`, then reapply the theme from the
repository root with `tools/theme.sh theme`.

### The patched app will not open

1. Confirm the patched package is listed (see above).
2. Confirm `dock_apps` and the `app_banners` entry both use the patched name
   and that you reapplied the theme.
3. If it still does not open, switch the dock back to stock YouTube (next
   section) so the tile works while you investigate. Keep the patched APK and
   `out/.signing/` so you can reinstall it.

### Switch the dock back to stock YouTube

In your local `theme/config.json`, put `com.google.android.youtube.tv` back in
`dock_apps` and the YouTube `app_banners` entry, then run
`tools/theme.sh theme`. This needs stock YouTube to be installed for the
current profile.

### Restore stock YouTube after `pm uninstall --user 0`

Removing stock YouTube with `pm uninstall --user 0` removes it from the current
profile only. Put it back before switching the dock to stock. This repository
has no script that reinstalls it, and this guide gives no restore command
that has not been checked. Reinstall YouTube from the TV's app store, or
install a Google-signed YouTube Android TV APK, then run the package check
above and reapply the theme.

### Setup problem or missed ad format

- Setup problem: the patched package is missing, the dock still names stock
  YouTube, or the theme was not reapplied. Videos behave like stock YouTube
  because stock YouTube is what opened.
- Missed ad format: the patched app opens and the dock names it, but an ad
  still appears. The patch filters known ad formats only, so some formats may
  pass through. Note the app version and where the ad appeared.
