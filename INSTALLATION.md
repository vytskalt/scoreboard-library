# Installation

Latest version: `2.5.0`

## Gradle

```kotlin
repositories {
  mavenCentral()
}

dependencies {
  val scoreboardLibraryVersion = "{VERSION HERE}"
  implementation("net.megavex:scoreboard-library-api:$scoreboardLibraryVersion")
  runtimeOnly("net.megavex:scoreboard-library-implementation:$scoreboardLibraryVersion")
  implementation("net.megavex:scoreboard-library-extra-kotlin:$scoreboardLibraryVersion") // Kotlin specific extensions (optional)

  // Add packet adapter implementations you want:
  runtimeOnly("net.megavex:scoreboard-library-modern:$scoreboardLibraryVersion") // 1.17+
  runtimeOnly("net.megavex:scoreboard-library-legacy:$scoreboardLibraryVersion") // 1.7.10-1.12.2

  // If using the PacketEvents implementation, scoreboard-library expects PacketEvents to be in the classpath.
  // Follow either of:
  // - https://github.com/retrooper/packetevents/wiki/Depending-on-pre%E2%80%90built-PacketEvents
  // - https://github.com/retrooper/packetevents/wiki/Shading-PacketEvents
  // Example how to load PacketEvents in your plugin:
  // https://github.com/retrooper/packetevents-example/blob/24f0c842d47362aef122b794dea29b8fee113fa3/thread-safe-listener/src/main/java/main/Main.java

  // If targeting a Minecraft version without native Adventure support, add it as well:
  implementation("net.kyori:adventure-platform-bukkit:4.3.4")
}
```

You will need to shade these dependencies and relocate them with something
like [Shadow](https://gradleup.com/shadow/).

## Maven

```xml
<dependencies>
  <dependency>
    <groupId>net.megavex</groupId>
    <artifactId>scoreboard-library-api</artifactId>
    <version>{VERSION HERE}</version>
  </dependency>
  <dependency>
    <groupId>net.megavex</groupId>
    <artifactId>scoreboard-library-implementation</artifactId>
    <version>{VERSION HERE}</version>
    <scope>runtime</scope>
  </dependency>
  <!-- Kotlin specific extensions (optional) -->
  <dependency>
    <groupId>net.megavex</groupId>
    <artifactId>scoreboard-library-extra-kotlin</artifactId>
    <version>{VERSION HERE}</version>
  </dependency>

  <!-- Add packet adapter implementations you want: -->
  <dependency>
    <groupId>net.megavex</groupId>
    <artifactId>scoreboard-library-modern</artifactId>
    <version>{VERSION HERE}</version>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>net.megavex</groupId>
    <artifactId>scoreboard-library-packetevents</artifactId>
    <version>{VERSION HERE}</version>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>net.megavex</groupId>
    <artifactId>scoreboard-library-legacy</artifactId>
    <version>{VERSION HERE}</version>
    <scope>runtime</scope>
  </dependency>

  <!--
    If using the PacketEvents implementation, scoreboard-library expects PacketEvents to be in the classpath.
    Follow either of:
    - https://github.com/retrooper/packetevents/wiki/Depending-on-pre-built-PacketEvents
    - https://github.com/retrooper/packetevents/wiki/Shading-PacketEvents
    Example how to load PacketEvents in your plugin:
    https://github.com/retrooper/packetevents-example/blob/24f0c842d47362aef122b794dea29b8fee113fa3/thread-safe-listener/src/main/java/main/Main.java 
    -->

  <!-- If targeting a Minecraft version without native Adventure support, add it as well: -->
  <dependency>
    <groupId>net.kyori</groupId>
    <artifactId>adventure-platform-bukkit</artifactId>
    <version>4.0.1</version>
  </dependency>
</dependencies>
```

You will need to shade these dependencies and relocate them with [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/).
