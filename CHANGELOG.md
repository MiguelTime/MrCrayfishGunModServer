# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## 1.4.21 - 2026-09-26

For Minecraft 1.21.1 on NeoForge.

### Added

- Add the `-PwithControllable` Gradle option to load Controllable in the development environment for controller testing.

### Changed

- Update controller integration to Controllable 0.25.4's binding, context, and event APIs. Controllable 0.25.4 is now the minimum supported version.
- Measure the controller's hold-to-unload gesture in game ticks instead of rendered frames. Hold for 40 ticks (approximately two seconds) to unload.
- Apply aiming sensitivity adjustments to the player's existing Controllable horizontal and vertical sensitivity settings.
- Add VulpesStella, FrostLeaf, and createmeow to the mod's author credits.

### Fixed

- Restore controller support for firing, aiming, steady aim, reloading, attachments, action hints, and workbench navigation.
- Clear controller button states when opening menus, losing window focus, or disconnecting the controller, and reset shooting synchronization when gameplay input is unavailable.
- Cancel pending controller reload and unload gestures when switching weapons to prevent actions from affecting a different weapon.
- Restore SimplePlanes weapon firing integration and prevent projectiles from hitting the plane carrying their shooter. Apply the integration mixin only when SimplePlanes is detected.
- Restore CMDCam compatibility so CGM does not override an active camera roll effect.
- Restore PlayerRevive bleeding-state checks and stop reflective calls after initialization fails to prevent null pointer exceptions.
