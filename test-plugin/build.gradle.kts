plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
  id("com.gradleup.shadow") version "9.3.1"
}

dependencies {
  compileOnly(libs.spigotApi)

  implementation(project(":scoreboard-library-api"))
  runtimeOnly(project(":scoreboard-library-implementation"))
  runtimeOnly(project(":scoreboard-library-modern"))

  implementation(libs.adventureApi)
  implementation(libs.adventureTextSerializerGson)
  implementation(libs.adventureTextSerializerLegacy)
}
