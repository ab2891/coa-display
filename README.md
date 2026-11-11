# CoA Display

Client-side Fabric mod for **Minecraft 26.1.2** with a Crown of Avarice coin HUD,
crown completion tracking, and experimental Diana burrow prediction logic.

## Install

Use Java 25 or newer and Fabric Loader 0.19.3 or newer. Put the following in your
26.1.2 instance's `mods` folder:

- `coa-display-1.1.0+mc26.1.2.jar` (not the sources JAR)
- Fabric API 0.152.1+26.1.2
- YetAnotherConfigLib (YACL) 3.9.4+26.1-fabric
- Optional: Mod Menu 18.0.0-beta.1 to open the configuration screen

Remove the previous CoA Display JAR first. Existing configuration and crown
statistics files keep their existing paths and formats. Open **Mods → Coa display
→ Configure** to change the HUD or use **Move HUD…** to drag it; Escape saves the
position and returns to the configuration screen.

Diana prediction remains experimental: the existing code logs guesses; it does
not draw burrow waypoints. Live Hypixel behavior must be checked in game.

## Build / IDE setup

Install JDK 25, set `JAVA_HOME` to that installation, and import this directory as
a Gradle project. Configure the IDE's Gradle JVM to use JDK 25, then reload the
Gradle project so Minecraft and Fabric dependencies are indexed.

```sh
./gradlew clean build
```

The installable JAR is in `build/libs/`. `./gradlew runClient` starts an isolated
development client in the ignored `run/` directory.

This port uses the unobfuscated Minecraft names and Fabric's non-remapping Loom
plugin. Old Yarn imports and `modImplementation` configuration are no longer used.

## In-game checks

- Launch 26.1.2 with the dependencies above and open the Mod Menu config screen.
- Move the HUD, press Escape, restart, and confirm its position persists.
- On Hypixel SkyBlock, equip a Crown of Avarice and confirm its coin total updates;
  remove it and confirm the HUD hides.
- Check crown completion tracking and the existing chat command behavior.
- During Diana, check particle detection and guess logs without duplicate events.

## Pack compatibility check

Built against the installed Prism `Skyblock 1.26.1.2` pack's dependency versions
listed above. Startup was checked with all 38 installed mod JARs in an isolated
development instance using fresh settings. Fabric loaded CoA alongside the pack
without a CoA dependency or mixin error and completed resource loading. Other
SkyBlock mods logged repository/data parsing errors; this was not a live Hypixel
test or a validation of the pack's saved settings.
