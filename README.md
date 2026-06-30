# Sprout — 3-8 year old learning app

Native Android app, written in **Kotlin + XML** (no Flutter/React Native),
covering all four tasks in the brief

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
app works fully offline)

## task2 mini interacttive screen screenshots
<img width="385" height="830" alt="Screenshot 2026-06-17 115336" src="https://github.com/user-attachments/assets/0180682e-ed83-4768-881f-c9021b217ec9" />
<img width="382" height="780" alt="Screenshot 2026-06-17 115416" src="https://github.com/user-attachments/assets/0c26cf63-60b3-433e-b696-b3d1076a4abe" />
<img width="377" height="830" alt="Screenshot 2026-06-17 115450" src="https://github.com/user-attachments/assets/f221b6b4-2dec-4363-a945-6c26eebcd45c" />
<img width="377" height="821" alt="Screenshot 2026-06-17 115523" src="https://github.com/user-attachments/assets/6107beb7-ec26-4580-93b9-a2a09d3c511e" />
<img width="412" height="811" alt="Screenshot 2026-06-17 115537" src="https://github.com/user-attachments/assets/1abba8c3-1e67-466d-9fa6-0d80b44c6ea2" />
<img width="383" height="793" alt="Screenshot 2026-06-17 115606" src="https://github.com/user-attachments/assets/fcf2b99b-dacd-4834-9865-c17d9c2129fa" />


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
room, etc
