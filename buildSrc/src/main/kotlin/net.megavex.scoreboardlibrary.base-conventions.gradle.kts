import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

plugins {
  `maven-publish`
  `java-library`
  id("net.kyori.indra")
  id("net.kyori.indra.publishing")
  id("com.gradleup.nmcp")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

repositories {
  mavenCentral()
  maven("https://oss.sonatype.org/content/repositories/snapshots")
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
  maven("https://hub.spigotmc.org/nexus/content/repositories/sonatype-nexus-snapshots/") {
    content {
      includeGroup("net.md-5")
    }
  }
}

indra {
  github("MegavexNetwork", "scoreboard-library") {
    ci(true)
  }
  mitLicense()
  reproducibleBuilds(true)

  javaVersions {
    target(8)
    minimumToolchain(21)
  }

  publishSnapshotsTo("forgejo", "https://git.megavex.net/api/packages/MegavexPublic/maven")
  configurePublications {
    pom {
      url = "https://github.com/MegavexNetwork/scoreboard-library"
      developers {
        developer {
          id = "vytskalt"
          name = "vytskalt"
          email = "vytskalt@protonmail.com"
        }
      }
    }
  }
}

nmcp {
  val centralUsername = findProperty("centralUsername") as String?
  val centralPassword = findProperty("centralPassword") as String?

  if (centralUsername != null && centralPassword != null) {
    publishAllPublications {
      username = centralUsername
      password = centralPassword
      publicationType = "USER_MANAGED"
    }
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs = mutableListOf("-Xlint:-deprecation,-unchecked")
}
