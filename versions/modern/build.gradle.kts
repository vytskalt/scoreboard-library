plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
  //id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

repositories {
  maven("https://repo.viaversion.com")
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base")) {
    //exclude(group = "org.spigotmc", module = "spigot-api")
  }
  compileOnly(libs.spigotApi)
  compileOnly("com.viaversion:viaversion-api:5.7.1")
  compileOnly("io.netty:netty-buffer:4.2.10.Final")
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
