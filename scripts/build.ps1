$ErrorActionPreference = 'Stop'
$Project = Split-Path -Parent $PSScriptRoot
$Sdk = if ($env:ANDROID_SDK_ROOT) { $env:ANDROID_SDK_ROOT } elseif ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { Join-Path $env:LOCALAPPDATA 'Android\Sdk' }
if (-not (Test-Path (Join-Path $Sdk 'platform-tools\adb.exe'))) { throw "Android SDK not found at $Sdk. Install Android SDK Platform 36 and Build Tools first." }
$env:ANDROID_HOME = $Sdk
$env:ANDROID_SDK_ROOT = $Sdk
if (-not $env:JAVA_HOME -and (Test-Path (Join-Path $env:LOCALAPPDATA 'AndroidBuild\jdk\jdk-17.0.20.1+1\bin\java.exe'))) { $env:JAVA_HOME = Join-Path $env:LOCALAPPDATA 'AndroidBuild\jdk\jdk-17.0.20.1+1' }
if ($env:JAVA_HOME) { $env:PATH = (Join-Path $env:JAVA_HOME 'bin') + ';' + $env:PATH }
Push-Location $Project
try {
    $InstalledGradle = Join-Path $env:LOCALAPPDATA 'AndroidBuild\gradle\gradle-8.13\bin\gradle.bat'
    if (Test-Path $InstalledGradle) { & $InstalledGradle assembleDebug }
    elseif (Test-Path '.\gradlew.bat') { & '.\gradlew.bat' assembleDebug }
    else { & 'gradle' assembleDebug }
    if ($LASTEXITCODE -ne 0) { throw 'Gradle build failed.' }
    $Dist = Join-Path $Project 'dist'
    New-Item -ItemType Directory -Force -Path $Dist | Out-Null
    Copy-Item -LiteralPath '.\app\build\outputs\apk\debug\app-debug.apk' -Destination (Join-Path $Dist 'LightLauncher.apk') -Force
    Write-Host "APK ready: $(Join-Path $Dist 'LightLauncher.apk')"
} finally { Pop-Location }
