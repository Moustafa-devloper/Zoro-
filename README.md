# NEXUS FF PANEL

NEXUS FF PANEL is a native Android Java gaming utility for Android devices. It is designed around a premium dark gaming dashboard, a floating overlay panel, honest system info, game selection, and optional Shizuku-aware system controls.

## Features

- Premium AMOLED dark dashboard
- Floating overlay permission flow and service lifecycle
- Gaming Mode toggle with persistent settings
- Game selection and launch via Android package manager APIs
- Screen Shift capability status and warning-based restrictions
- Honest performance monitoring: FPS and device status are labeled as unavailable when a real source does not exist
- Optional Shizuku status and permission detection
- Strong permission and system requirement handling

## Project structure

- `app/src/main/java/com/nexus/ffpanel` — Java application code
- `app/src/main/res` — XML layouts, themes, drawables, strings, and colors
- `app/src/main/AndroidManifest.xml` — Android manifest with required permissions and service declarations

## Requirements

- Android Studio or CodeAssist project import
- Android SDK with API 26+ support
- Java 17 compatible toolchain

## Build

1. Open the repository in Android Studio or CodeAssist.
2. Let Gradle sync.
3. Build the project using the Android app configuration.
4. Install the APK on a supported device.

## Important limitations

- Screen Shift does not claim unsupported display changes on every device.
- FPS, temperature, and performance metrics are reported only when valid Android APIs exist; otherwise they are shown as unavailable.
- The app avoids fake overclocking, unsupported memory manipulation, and game automation.

## Shizuku note

The project includes a status-only Shizuku detection path and avoids pretending that privileged system operations work without actual support. If required by the device, install Shizuku and grant permission before using advanced privileged features.

## License

This project is distributed under the MIT License unless a different license is added later.
