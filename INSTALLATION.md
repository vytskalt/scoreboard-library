# Installation

Latest version: `2.6.0`

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

  <!-- If targeting a Minecraft version without native Adventure support, add it as well: -->
  <dependency>
    <groupId>net.kyori</groupId>
    <artifactId>adventure-platform-bukkit</artifactId>
    <version>4.0.1</version>
  </dependency>
</dependencies>
```

You will need to shade these dependencies and relocate them with [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/).
