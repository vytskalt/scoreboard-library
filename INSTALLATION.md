# Installation

Latest version: `2.8.2`

## Gradle

```kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("net.megavex:scoreboard-library-api:2.8.2")
  runtimeOnly("net.megavex:scoreboard-library-implementation:2.8.2")

  // If targeting a server version without native Adventure support (Spigot or older than 1.16.5 Paper), add it as well:
  implementation("net.kyori:adventure-platform-bukkit:4.4.1")
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
    <version>2.8.2</version>
  </dependency>
  <dependency>
    <groupId>net.megavex</groupId>
    <artifactId>scoreboard-library-implementation</artifactId>
    <version>2.8.2</version>
    <scope>runtime</scope>
  </dependency>

  <!-- If targeting a server version without native Adventure support (Spigot or older than 1.16.5 Paper), add it as well: -->
  <dependency>
    <groupId>net.kyori</groupId>
    <artifactId>adventure-platform-bukkit</artifactId>
    <version>4.4.1</version>
  </dependency>
</dependencies>
```

You will need to shade these dependencies and relocate them with [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/).

## ViaVersion awareness

For [ViaVersion awareness](https://github.com/vytskalt/scoreboard-library?tab=readme-ov-file#features) to be enabled, make sure to add `ViaVersion`
to `softdepend` or `depend` of your `plugin.yml`:

```yaml
softdepend: ["ViaVersion"]
# or
depend: ["ViaVersion"]
```
