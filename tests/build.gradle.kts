plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

dependencies {
  implementation(project(":scoreboard-library-api"))
  implementation(project(":scoreboard-library-implementation"))

  implementation(libs.spigotApi)

  implementation(libs.adventureApi)
  implementation(libs.adventureTextSerializerGson)
  implementation(libs.adventureTextSerializerLegacy)
}

tasks.named("test") {
  enabled = false
  dependsOn(tasks.named("runTests"))
}

tasks.register<JavaExec>("runTests") {
  group = "verification"
  classpath = sourceSets.main.get().runtimeClasspath
  mainClass.set("net.megavex.scoreboardlibrary.tests.Main")
}
