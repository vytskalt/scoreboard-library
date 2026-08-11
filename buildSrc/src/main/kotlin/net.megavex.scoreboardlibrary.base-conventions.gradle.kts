import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

plugins {
  `java-library`
  `maven-publish`
  signing
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

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8

  withSourcesJar()
  withJavadocJar()
}

tasks.withType<AbstractArchiveTask>().configureEach {
  isPreserveFileTimestamps = false
  isReproducibleFileOrder = true
}

publishing {
  publications.withType<MavenPublication>().configureEach {
    from(components["java"])

    pom {
      name = project.name
      description = project.provider { project.description }
      url = "https://github.com/vytskalt/scoreboard-library"

      licenses {
        license {
          name = "The MIT License"
          url = "https://opensource.org/licenses/MIT"
          distribution = "repo"
        }
      }

      scm {
        connection =
          "scm:git:https://github.com/vytskalt/scoreboard-library.git"
        developerConnection =
          "scm:git:ssh://git@github.com/vytskalt/scoreboard-library.git"
        url =
          "https://github.com/vytskalt/scoreboard-library"
      }

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

signing {
  useGpgCmd()
  sign(publishing.publications)
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
