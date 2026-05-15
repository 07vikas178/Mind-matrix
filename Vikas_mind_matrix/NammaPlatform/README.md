# Namma-Platform 🚆

A simplified, **Kannada-first** railway assistant for small Indian railway
stations. Built as the *MindMatrix VTU Internship* infrastructure project.

> Helps rural and elderly passengers find the right platform and the right
> coach (General / Ladies) without depending on inaudible English / Hindi
> announcements.

---

## ✨ Features

| Feature | Description |
|---|---|
| **Live Station** | Big-tile picker for nearby stations (Kannada + English). |
| **Next 3 Trains** | Anti-clutter list — only the next 3 upcoming trains. |
| **Platform Info** | Large yellow platform-number card per train. |
| **Coach Layout** | Horizontal scrollable strip with colour-coded coaches.<br/>🟡 GEN, 🩷 LADIES, 🚂 ENGINE highlighted. |
| **Help Me (TTS)** | Speaks the announcement aloud in **Kannada** (falls back to Hindi → English if no voice installed). |
| **High-Contrast UI** | Indian-Railways navy + bright yellow, large fonts. |
| **Offline-first** | All data ships in `assets/stations.json`. No internet required. |

---

## 🏛️ Architecture

```
 ┌─ PRESENTATION ─────────────────────────────────┐
 │  MainActivity        →    PlatformActivity     │
 │     │                          │               │
 │     ▼                          ▼               │
 │  HomeViewModel           PlatformViewModel     │
 │                              │                 │
 │                              ▼                 │
 │                          TtsHelper             │
 └────────┬───────────────────────────────────────┘
          ▼
 ┌─ DATA ─────────────────────────────────────────┐
 │  StationRepository                             │
 │    └─► JsonStationDataSource                   │
 │              └─► assets/stations.json          │
 └────────────────────────────────────────────────┘
```

* MVVM (ViewModel + LiveData)
* Repository pattern with a `StationDataSource` interface (DIP / SOLID)
* Coroutines for IO
* No third-party JSON library — uses built-in `org.json`

### Folder structure

```
NammaPlatform/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/wrapper/gradle-wrapper.properties
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── assets/stations.json
        ├── java/com/nammaplatform/app/
        │   ├── MainActivity.kt
        │   ├── PlatformActivity.kt
        │   ├── data/
        │   │   ├── StationDataSource.kt
        │   │   ├── JsonStationDataSource.kt
        │   │   └── StationRepository.kt
        │   ├── model/
        │   │   ├── CoachType.kt
        │   │   ├── Train.kt
        │   │   └── Station.kt
        │   ├── ui/
        │   │   ├── home/
        │   │   │   ├── HomeViewModel.kt
        │   │   │   └── StationAdapter.kt
        │   │   └── platform/
        │   │       ├── PlatformViewModel.kt
        │   │       ├── TrainAdapter.kt
        │   │       └── CoachStripView.kt
        │   └── util/TtsHelper.kt
        └── res/
            ├── layout/      (activity_*.xml, item_*.xml)
            ├── drawable/    (coach tiles, cards, button)
            ├── values/      (colors, strings, themes, dimens)
            ├── xml/         (backup_rules, data_extraction_rules)
            └── mipmap-anydpi-v26/
```

---

## 🚀 Setup & Run

### Prerequisites
* **Android Studio Iguana** (or newer) — *Hedgehog 2023.1.1+* will also work
* **JDK 17** (bundled with Android Studio)
* An Android device or emulator running **Android 7.0+ (API 24)**

### Steps

1. **Open the project**
   * Android Studio → **Open** → select `C:\New folder\NammaPlatform`
   * Wait for Gradle sync. The first sync downloads Gradle 8.7 and the AGP 8.5.2 + Kotlin 1.9.24 dependencies.

2. **Generate the Gradle wrapper** *(only if Studio doesn't auto-create `gradlew`)*
   ```
   gradle wrapper --gradle-version 8.7
   ```
   *(skip if Studio already runs the build)*

3. **Run the app**
   * Click ▶ **Run 'app'** (or `Shift+F10`).

### Hearing Kannada audio

The app uses Android's built-in TTS engine. To hear **Kannada (ಕನ್ನಡ)**:

1. On the device, open **Settings → System → Languages & input → Text-to-speech output**.
2. Tap the gear icon next to your engine (Google TTS recommended) → **Install voice data**.
3. Pick **Kannada (India)** → install.
4. Return to the app and tap the big yellow button.

If no Kannada voice is installed, the app falls back to Hindi → English and shows a one-time toast.

---

## 🧪 Sample data

`assets/stations.json` ships with 4 Karnataka stations — Mysuru, Bengaluru
(KSR), Mangaluru, and Hubballi — and 3–5 trains per station so you have
something to demo. Add more stations by appending to the same JSON file;
no code changes needed.

---

## ✅ Success criteria coverage (per PDF)

| PDF requirement | Where implemented |
|---|---|
| Coach Layout visually clear | `CoachStripView.kt` + `bg_coach_*.xml` |
| Match the train name | `tvSelectedTrain` in `activity_platform.xml` |
| Kannada audio loud and clear | `TtsHelper.kt` (rate 0.9×, pitch 1.05×, Kannada locale) |
| High-contrast (Blue/Yellow) UI | `colors.xml`, `themes.xml` |
| Horizontal ScrollView for coaches | `CoachStripView` extends `HorizontalScrollView` |
| Local JSON for coach data | `assets/stations.json` + `JsonStationDataSource.kt` |
| Next 3 trains only | `StationRepository.nextTrains(limit = 3)` |

---

## 📜 License

MIT — student / internship project.
