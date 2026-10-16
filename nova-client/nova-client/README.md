# Nova Client (Fabric, Minecraft 1.21.1)

## Build
1. Install JDK 21.
2. Add the Gradle wrapper (one time):  `gradle wrapper --gradle-version 8.10`
   (or copy the `gradlew`, `gradlew.bat`, `gradle/` folder from https://github.com/FabricMC/fabric-example-mod)
3. `./gradlew build`  -> jar appears in `build/libs/nova-client-1.0.0.jar`
4. `./gradlew runClient` to test it directly.

Install: Fabric Loader 0.16+ and Fabric API for 1.21.1, then drop the jar (plus Sodium, Lithium,
FerriteCore, ImmediatelyFast, Entity Culling) into your `mods` folder.

If dependency versions fail to resolve, get current ones from https://fabricmc.net/develop and
edit gradle.properties.

## Keys
C = Zoom (hold), Right Shift = Nova Settings (rebindable in Controls).
