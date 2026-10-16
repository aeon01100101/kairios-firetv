# Kairios for Fire TV — Special Edition

A sideloadable Fire TV app that puts a **boot menu** in front of the TV:
when the Fire TV finishes starting, a menu pops up and you choose
**Kairios** or **Fire TV**.

- **Kairios** — its own simple home screen: big tiles for every app on the
  TV, a clock, and buttons for Fire TV Home, Boot Menu, and TV Settings.
- **Fire TV** — Amazon's normal home screen, untouched.

## The honest limit

A sideloaded app **cannot** run before Fire OS or replace the bootloader —
Amazon locks that down and there is no root on stock Fire TVs. What this
app does is the closest real thing:

1. It listens for `BOOT_COMPLETED` and pops the menu up the moment the TV
   finishes starting.
2. It also registers as a HOME / leanback launcher app, so it can be picked
   as the Home-button app where Fire OS allows it.

## Install (one time)

1. Copy `kairios-firetv-v1.0.0.apk` to the Fire TV (Downloader app, USB, or
   `adb install`).
2. Install it (allow "apps from unknown sources" for the installer app if
   Fire OS asks).
3. Open **Kairios for Fire TV** once.
4. Optional: use the **"Set Kairios as your Home button app"** button on the
   menu. On some Fire OS versions Amazon blocks changing the Home app — the
   boot pop-up still works either way.
5. Restart the TV. The menu pops up when startup finishes.

## Using it

- Pick a side with the remote and press OK.
- **Show this menu every time the TV starts: ON/OFF** — turn it OFF and the
  TV goes straight to whichever side you picked last. Open the Kairios app
  from the Fire TV apps row any time to get the menu back.
- Inside Kairios, **Back** returns to the boot menu.

## Build notes

- Package `com.kairios.firetv`, minSdk 24 (Fire OS 6+), targetSdk 34.
- Pure Java, zero third-party dependencies.
- Signed with a permanent keystore kept **outside** this repo, so updates
  always install over the top. Cert SHA-256:
  `a3bdeabd324cd97eeba4884b9dad59e453645dc88b8f8660dd2938265fbf163c`
- Boot routing logic is host-tested: `host-test/run_tests.sh` (13 checks).
- Build: Android Gradle Plugin 8.5.2, `./gradlew :app:assembleRelease`
  (or any Gradle 8.7+ with JDK 17 and Android SDK 35).

Status: built and bench-validated (build, signature, binary manifest).
**Not yet device-tested** — first install on the TV is the real test.
