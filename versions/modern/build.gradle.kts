plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
  //id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base")) {
    //exclude(group = "org.spigotmc", module = "spigot-api")
  }
  compileOnly(libs.spigotApi)
  //paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
}

tasks {
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
    // Kept for backwards compatibility
    artifact(tasks.jar) {
      classifier = "mojmap"
    }

    artifact(tasks.jar)
    artifact(tasks.javadocJar)
    artifact(tasks.sourcesJar)
  }
}
