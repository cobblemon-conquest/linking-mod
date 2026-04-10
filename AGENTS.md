# AGENTS.md

## Scope
- This repository is an Architectury multi-project mod: `:common` + `:fabric` (`settings.gradle`).
- Root `src/main/kotlin` contains older single-module Fabric code and is not wired into current subproject builds.

## Big-Picture Architecture
- Shared game logic lives in `common/src/main/java/dev/albercl/conquestmod/common/`.
- Fabric bootstrap lives in `fabric/src/main/java/.../ConquestModFabric.java` and calls `ConquestMod.init()` from `:common`.
- Registration flow is explicit: `ConquestMod.init()` -> `ConquestPokeBalls.register()` -> `ConquestItems.register()`.
- Custom Poke Balls clone Cobblemon Cherish Ball behavior in `ConquestPokeBalls.createFromCherishBall(...)`.
- Cobblemon registry integration uses reflection (`PokeBalls.defaults` field) in `ConquestPokeBalls.registerInCobblemonRegistry(...)`.

## Build, Run, and Debug Workflows
- Use Java 21 for local builds (`build.gradle` sets source/target/release 21).
- Primary build command:
  - `./gradlew build`
- Useful module-scoped commands:
  - `./gradlew :common:build :fabric:build`
  - `./gradlew :fabric:runClient`
  - `./gradlew :fabric:runServer`
- Packaging path is Fabric-specific shadow/remap:
  - `:fabric:shadowJar` -> `:fabric:remapJar` (`fabric/build.gradle`).
- CI (`.github/workflows/build.yml`) runs `./gradlew build` and publishes release artifacts from `build/libs/conquest-mod-*.jar`.

## Project-Specific Conventions
- Mod id in active modules is `conquest_mod` (`ConquestMod.MOD_ID`, `fabric/src/main/resources/fabric.mod.json`).
- Assets/lang/model paths in active code use `assets/conquest_mod/...` (underscore, not hyphen).
- New shared gameplay features should be added in `:common` first, then exposed via Fabric entrypoints.
- Keep Cobblemon item + pokeball IDs aligned across:
  - `ConquestItems.java`
  - `ConquestPokeBalls.java`
  - `common/src/main/resources/assets/conquest_mod/lang/en_us.json`
  - `common/src/main/resources/assets/conquest_mod/models/item/*.json`

## Integration Points and Risks
- External deps come from Fabric/Architectury/Cobblemon (`build.gradle`, `gradle.properties`).
- Cobblemon registry write is reflective and version-sensitive; changes in Cobblemon internals can break startup.
- `fabric/src/main/resources/fabric.mod.json` entrypoints currently reference `dev.albercl.conquestmod.fabric...`, while source package is `dev.albercl.conquestmod.common.fabric...`; verify before release.
- No `src/test` trees are present in `:common` or `:fabric`; `test` tasks exist but currently have no project tests.

## Existing AI Instructions Scan
- Required glob scan (`**/{.github/copilot-instructions.md,AGENT.md,AGENTS.md,CLAUDE.md,.cursorrules,.windsurfrules,.clinerules,.cursor/rules/**,.windsurf/rules/**,.clinerules/**,README.md}`) returned no files at generation time.

