plugins {
  id("com.gradleup.shadow") version "9.3.1"
}

dependencies {
  compileOnly(libs.spigotApi)

  implementation(project(":scoreboard-library-api"))
  implementation(project(":scoreboard-library-implementation"))

  compileOnly(libs.adventureApi)
  compileOnly(libs.adventureTextSerializerGson)
  compileOnly(libs.adventureTextSerializerLegacy)

  // bundled adventure:
  implementation(libs.adventureApi)
  implementation(libs.adventureTextSerializerGson) {
    //exclude("com.google.code.gson")
  }
  implementation(libs.adventureTextSerializerLegacy)
}

tasks.shadowJar {
//  relocate("com.google.gson", "net.megavex.scoreboardlibrary.testplugin.lib.gson")
//  relocate("net.kyori", "net.megavex.scoreboardlibrary.testplugin.lib.kyori")
}
