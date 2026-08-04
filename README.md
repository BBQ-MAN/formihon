# K만화 (KMana) Mihon Extension

This repository contains the source code and extension repository index for the KMana (`https://kmana10.net/`) Mihon Extension.

## 🚀 How to Add This Repository to Mihon

Mihon supports adding custom third-party extension repositories via `index.json`.

1. Host the `index.json` file on GitHub Pages, Raw GitHub content, or your own server.
2. Open the **Mihon App**.
3. Go to **Settings** -> **Browse** -> **Extension repositories**.
4. Tap **Add** and paste the URL to your hosted `index.json` file.
5. Go to the **Extensions** tab to install the `K만화 (KMana)` extension.

## 🛠️ Build Instructions

To build the APK manually:

1. Clone this repository.
2. Ensure you have the Android SDK installed.
3. Run the following command to build the extension APK:
   ```bash
   ./gradlew assembleRelease
   ```
4. The generated APK will be available in the `build/outputs/apk/release/` directory.

## ⚠️ Notes on Dynamic Domain

Korean manga sites often change domains (e.g., `kmana10.net` to `kmana11.net`) to bypass blocking.
To handle this, this extension includes a dynamic URL override in the Extension Preferences. 

If the site stops working:
1. Go to your **Sources** tab.
2. Long-press on **K만화 (KMana)** and open its Settings.
3. Update the Base URL to the newly working domain (e.g., `https://kmana11.net`).
4. Restart Mihon and try again.
