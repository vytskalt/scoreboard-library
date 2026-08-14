plugins {
  alias(libs.plugins.shadow)
}

dependencies {
  compileOnly(libs.spigotApi)

  implementation(project(":scoreboard-library-api"))
  implementation(project(":scoreboard-library-implementation"))

  //compileOnly(libs.bundles.adventure)

  // bundled adventure:
  implementation(libs.bundles.adventure) {
    //exclude("com.google.code.gson")
  }
}

tasks.shadowJar {
  //relocate("com.google.gson", "net.megavex.scoreboardlibrary.testplugin.lib.gson")
  //relocate("net.kyori", "net.megavex.scoreboardlibrary.testplugin.lib.kyori") {
  //  skipStringConstants = true
  //}
}
