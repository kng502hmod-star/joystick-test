# Joystick Visual 1.0 (Android Studio source project)

This independent project draws a decorative joystick using a standard Android overlay. It does not inject into, hook, read, or modify another app; it does not simulate or dispatch input. There is no network access or activation system. The overlay is visible and must be started manually. It is not a replacement for internal game rendering logic. Some applications or OEM modes can hide or block overlays, and compatibility with any specific game or tablet is unverified. Respect the rules of the application on which you use overlays.

## Build

1. Open this folder in Android Studio, with JDK 17, Android SDK Platform 35 and Gradle 8.9. The build uses Android Gradle Plugin 8.7.3. An internet connection may be needed to obtain the build dependencies.
2. Sync Gradle. In the IDE choose Build > Build App Bundle(s) / APK(s) > Build APK(s) (or equivalent). Alternatively, with a compatible local Gradle installation, run `gradle :app:assembleDebug`.
3. The debug APK is normally generated at `app/build/outputs/apk/debug/app-debug.apk`. Install it on your own Android device. Android 8.0 or later is required.

## Use

1. Open Joystick Visual and grant the Display over other apps permission. Notification permission enables the foreground-service Stop shortcut.
2. Set the position, size, opacity, speed, and animation style. Tap Start / apply. The small overlay animates independently and is deliberately non-touchable.
3. Return to Joystick Visual and tap Stop overlay, or tap Stop in the notification. The service is not restarted automatically after a reboot or kill.

The opacity is capped at 70% to stay below Android 12's default 80% obscuring-opacity threshold for a single eligible touch-through overlay. Other applications can still reject obscured touches or hide overlays. Do not change device security protections to work around those decisions.

## Verification

The animation mathematics can be tested independently with JDK 17:

```sh
javac -d /tmp/joystick-visual-test app/src/main/java/com/example/joystickvisual/Motion.java tests/MotionTest.java
java -cp /tmp/joystick-visual-test MotionTest
```

The Android app has not been compiled or tested on an actual device in the authoring environment because the Android SDK and Gradle are unavailable. This archive contains source, not an APK.
