# File encryption sample for Android

##Build

```
./gradlew clean assembleDebug
```

Find APK file at `samples/android-crypto/app/build/outputs/apk` directory.

If your device connected to your computer, use the next command to install APK to device

```
$ANDROID_HOME/platform-tools/adb install -r app/build/outputs/apk/app-debug.apk
```

## Usage

* Press `Select file` button to select file for encryption
* Press `Encode` button to encrypt file
* Find encrypted file in the directory described below. Encrypted file has `.encrypted` suffix
* Press `Select file` button to select encrypted file for decryption
* Press `Decode` button tp decrypt selected file. If file couldn't be decrypted, correspont message appeared

You can find processed files at the directory

```
/storage/emulated/0/Android/data/com.virgilsecurity.samples.crypto/files/Documents/Processed/
```

Take into account that external directory is device-specific and could differ from the directory provided above.
