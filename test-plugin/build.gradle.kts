plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
  id("com.gradleup.shadow") version "9.3.1"
}

dependencies {
  compileOnly(libs.spigotApi)

  implementation(project(":scoreboard-library-api"))
  implementation(project(":scoreboard-library-implementation"))
  implementation(project(":scoreboard-library-modern"))

  compileOnly(libs.adventureApi)
  compileOnly(libs.adventureTextSerializerGson)
  compileOnly(libs.adventureTextSerializerLegacy)

  // bundled adventure:
  implementation(libs.adventureApi)
  implementation(libs.adventureTextSerializerGson)
  implementation(libs.adventureTextSerializerLegacy)
}
