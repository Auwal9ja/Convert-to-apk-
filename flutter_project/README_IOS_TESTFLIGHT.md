# Complete Flutter & iOS (Xcode / TestFlight) Build Guide

An samar da cikakken kundin Flutter project a cikin folder mai suna `flutter_project/` wanda ya kunshi dukkan manhajarka (Saloli, Al-Qur'ani mai murya, Alƙibla Compass, Addu'o'i da Saituna, OneSignal da AdMob).

---

## 1. Yadda Zaka Fitar Da Files Din (Export / Download)
1. Danna alamar **Download ZIP / Export** a menu na sama ko a Settings na AI Studio.
2. Zazzage dukkan ZIP file din zuwa kan kwamfutarka (Mac).
3. Bude (unzip) fayil din, zaka ga babban folder mai suna `flutter_project`.

---

## 2. Matakan Fara Aiki a Xcode (iOS Build)

A kan na'urarka ta Mac:

### Mataki 1: Bude Terminal a cikin `flutter_project`
```bash
cd /path/to/your/unzipped_folder/flutter_project
flutter pub get
```

### Mataki 2: Saita Pods na iOS
```bash
cd ios
pod install
cd ..
```

### Mataki 3: Bude Project a Xcode
```bash
open ios/Runner.xcworkspace
```
*(Tabbatar ka bude `.xcworkspace` ba wai `.xcodeproj` ba).*

---

## 3. Saita Sign in & Capabilities a Xcode
1. A cikin Xcode, danna babban folder mai suna **Runner** a gefen hagu.
2. Je shafin **Signing & Capabilities**:
   - Saka alamar kaska (check) a kan **Automatically manage signing**.
   - Zabi **Team** dinka na Apple Developer.
   - Saita **Bundle Identifier** na musamman (misali `com.dbtech.islamicapp`).
3. Danna **+ Capability** domin tabbatar da:
   - **Push Notifications** (don OneSignal).
   - **Background Modes** (Audio, Remote notifications, Background fetch).

---

## 4. Gina App Don TestFlight (Build & Distribute)

1. A cikin Terminal:
   ```bash
   flutter build ipa --release
   ```
2. Ko a cikin Xcode:
   - Zabi target na sama ya zama **Any iOS Device (arm64)**.
   - Danna **Product** > **Archive** a menu na sama.
   - Idan ya gama, taga (window) na **Organizer** zai bude.
   - Danna **Distribute App** > **App Store Connect** > **Upload**.

---

## 5. Kaddamarwa a TestFlight (App Store Connect)
1. Shiga [App Store Connect](https://appstoreconnect.apple.com).
2. Je sashen **Apps** > Zabi manhajarka > Shiga shafin **TestFlight**.
3. Da zarar Apple ya gama sarrafa IPA din da ka tura (Processing Complete), zaka iya saka email na testers a **Internal Testing** ko **External Testing**.
