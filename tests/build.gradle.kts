dependencies {
  testImplementation(project(":scoreboard-library-api"))
  testImplementation(project(":scoreboard-library-implementation"))

  testImplementation(libs.spigotApi)
  testImplementation(libs.bundles.adventure)
}

tasks.named("test") {
  enabled = false
  dependsOn(tasks.named("runTests"))
}

tasks.register<JavaExec>("runTests") {
  group = "verification"
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("net.megavex.scoreboardlibrary.tests.Main")
}
