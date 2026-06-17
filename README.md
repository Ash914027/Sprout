# Sprout — take-home assignment

Native Android app, written in **Kotlin + XML** (no Flutter/React Native),
covering all four tasks in the brief.

## What's where

| Task | Deliverable | Where to find it |
|---|---|---|
| 1. Engagement loop design | 1–2 page write-up | `docs/task1_engagement_loop.md` |
| 2. Mini interactive screen | Drawing activity: tap-to-pick colors, sparkle particle animation, sound, 60s timer (a sped-up demo of the 5-minute loop), confetti reward on completion | `DoodleActivity.kt` + `DoodleCanvasView.kt` + `activity_doodle.xml` |
| 3. Drop-off diagnosis | ≤200 word bullet write-up | `docs/task3_dropoff_diagnosis.md` |
| 4. Live device integration | "Photo Hunt": CameraX live preview + capture, on-device ML Kit image labeling, 5-item scavenger hunt with progress dots and rewards | `PhotoHuntActivity.kt` + `activity_photo_hunt.xml` |

Both interactive screens live in one combined app (`MainActivity` is a small
home screen linking to each), and share a reusable `RewardDialogFragment`
(confetti + chime + sticker) and `SoundBoard` (tiny synthesized sound
effects in `res/raw`, generated locally — no external audio assets, so the
app works fully offline).

## How to open and run it

1. Open Android Studio (Iguana/Koala or newer recommended) → **Open** →
select the `SproutApp` folder.
2. Let Gradle sync. It will need internet access on *your* machine the
first time, to download:
   - CameraX (`androidx.camera:*`)
   - ML Kit Image Labeling, bundled/offline model (`com.google.mlkit:image-labeling`)
   - AndroidX/Material libraries
3. Run on a **physical device** for Task 4 (the emulator's virtual camera
gives poor image-labeling results — a real camera works much better).
4. Grant the camera permission when prompted inside the Photo Hunt screen.

> Note: this project was authored as source files in a sandboxed
> environment without the Android SDK / Gradle Android plugin installed, so
> it has not been compiled here. It's structured as a standard Android
> Studio project (Kotlin DSL Gradle, ViewBinding, minSdk 24, compileSdk 34,
> JDK 17) and should sync and run normally once opened — if anything
> doesn't sync, the most common fix is simply letting Android Studio
> upgrade the Gradle/AGP version it suggests.

## Design notes

- **Photo Hunt's identification logic is intentionally generous.** Ages
3–5 don't benefit from "try again, that's wrong" — so any confidently
recognized object is celebrated, with extra-specific praise when it happens
to match the current target (flower / book / toy / cup / shoe). This keeps
the loop frustration-free while still teaching object names.
- **Everything is bundled/offline-first.** Sounds are synthesized locally
(no streamed audio), and the ML Kit labeler uses the bundled (non-Play
Services) model, so neither screen requires the device to be online at
runtime — appropriate for a kids' app that might run in a car, a waiting
room, etc.
