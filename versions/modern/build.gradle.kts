plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
  alias(libs.plugins.paperweight)
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base")) {
    exclude(group = "org.spigotmc", module = "spigot-api")
  }
  paperweight.paperDevBundle(libs.versions.devBundle.get())
}

tasks {
  compileJava {
    // Workaround to get it to compile targeting Java 8 while using NMS which targets 17
    // Why does this work? I have no idea
    options.release = null
  }

  // https://github.com/unnamed/hephaestus-engine/blob/db6deabe1ddb0a549306b0c4b519c3e79e6f1ea8/runtime-bukkit/adapt-v1_20_R3/build.gradle.kts#L25
  reobfJar {
    outputJar = file("build/libs/scoreboard-library-modern-$version-reobf.jar")
  }

  assemble {
    dependsOn(reobfJar)
  }

  javadoc {
    exclude("**")
  }
}

java {
  disableAutoTargetJvm()
}

indra {
  includeJavaSoftwareComponentInPublications(false)
}

publishing {
  publications.getByName<MavenPublication>("maven") {
    artifact(tasks.jar) {
      classifier = "mojmap"
    }

    artifact(tasks.reobfJar)
    artifact(tasks.javadocJar)
    artifact(tasks.sourcesJar)
  }
}
