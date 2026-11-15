# Build APK with GitHub Actions

This project builds with Java 17, Gradle 8.9, Android Gradle Plugin 8.7.3 and Android SDK 35.

1. Create a new GitHub repository (private if you want to keep source private).
2. Upload **the contents of the joystick-overlay folder** to the repository root, including the hidden `.github` folder. You can also push the folder using Git.
3. Open the repository **Actions** tab and select **Build Android APK**.
4. Click **Run workflow** (or push to main/master).
5. When the run succeeds, open it and download **JoystickVisual-debug-APK** from Artifacts.
6. Unzip the artifact to get `app-debug.apk`.

Note: This is a debug APK for testing, not a signed production release. It may need the overlay permission on the device. This workflow does not guarantee compatibility with every phone or game.
