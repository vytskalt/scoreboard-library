plugins {
  alias(libs.plugins.shadow)
}

dependencies {
  compileOnly(libs.spigotApi)

  implementation("net.megavex:scoreboard-library-api:2.8.2")
  implementation("net.megavex:scoreboard-library-implementation:2.8.2")

  implementation(libs.bundles.adventure)
}

tasks.shadowJar {
  relocate("net.kyori", "net.megavex.scoreboardlibrary.testplugin.lib.kyori") {
    skipStringConstants = true
  }
}
