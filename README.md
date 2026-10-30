# LightLauncher

LightLauncher is a remote-first Android home screen for Android TV boxes and phones. It is designed for landscape displays and supports Android 7.1.2 (API 25) and later.

## What it includes

- First-run setup for language, interface scale, wallpaper, and the default Home app.
- English, Ukrainian, Spanish, French, German, Polish, Italian, Turkish, and Arabic UI text. Clock and date formatting follow the selected language.
- Seven built-in wallpapers with named previews, plus image selection from device storage.
- A Home screen with up to five pinned apps, a separate All apps grid, and a RAM cleanup control.
- D-pad and OK navigation, visible focus styling, Wi-Fi status colors, settings shortcuts, a translated FAQ, and diagnostic logs.
- Settings for interface scale, wallpaper, language, Android settings, and choosing LightLauncher as the Home app.

## Android app ID

The application ID and Gradle namespace are `com.android.lightlauncher`. The visible app name is `LightLauncher`.


## Requirements

- Windows PowerShell
- JDK 17
- Android SDK Platform 36 and Android Build Tools
- Gradle 8.13 (the build script uses the local Gradle installation when available, otherwise the included wrapper)

Set `JAVA_HOME` and either `ANDROID_SDK_ROOT` or `ANDROID_HOME` if they are not already configured. The build script defaults to the standard Android SDK under `%LOCALAPPDATA%\Android\Sdk` and also detects the JDK installed by Codex on this PC.

## Build the APK

From the project root in PowerShell:

```powershell
.\scripts\build.ps1
```

The script builds the debug APK and copies it to:

```text
dist\LightLauncher.apk
```

To run Gradle directly instead:

```powershell
.\gradlew.bat assembleDebug
```

Gradle's output is:

```text
app\build\outputs\apk\debug\app-debug.apk
```

## Install manually

Connect an Android device with USB debugging enabled, then run:

```powershell
adb install .\dist\LightLauncher.apk
```

To launch it from ADB:

```powershell
adb shell monkey -p com.android.lightlauncher -c android.intent.category.LAUNCHER 1
```

Choose LightLauncher as the default Home app when Android prompts, or open **Settings → Apps → Default apps → Home app**. Menu names vary by Android version and ROM.

## Remote controls

- **Up / Down / Left / Right:** move focus through cards and apps.
- **OK / Enter:** open the focused item.
- **Long-press OK on an app:** pin or unpin it (up to five pinned apps).
- **Home:** return to LightLauncher's pinned-app screen.
- **Back:** return to the previous launcher page.

## Project layout

```text
app/src/main/AndroidManifest.xml        Android launcher entry point and app label
app/src/main/java/com/android/lightlauncher/MainActivity.java  Launcher UI and behavior
app/src/main/res/                        Theme and launcher icon
app/src/main/assets/wallpapers/          Bundled wallpaper images
gradle/                                  Gradle wrapper
gradlew.bat                              Windows Gradle wrapper command
scripts/build.ps1                        Build and copy the debug APK
```

## Notes for contributors

- Wallpaper image files live in `app/src/main/assets/wallpapers/`. The picker uses friendly localized names for the seven bundled `image1.jpg`–`image7.jpg` assets.
- The debug APK is signed with a debug key and is intended for development and sideloading, not Play Store release.
- Android and custom ROMs limit background-process cleanup; the memory button can request cleanup but cannot force Android to terminate essential services.
- Before publishing the wallpaper assets, confirm that you have permission to redistribute each image.
- Generated build output, local SDK settings, and APKs are excluded by `.gitignore`.
