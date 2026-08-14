plugins {
  `java-library`
  `maven-publish`
  signing
  alias(libs.plugins.nmcpAggregation)
}

allprojects {
  version = "2.8.2"
  group = "net.megavex"
  description = "Powerful packet-level scoreboard library for Minecraft Paper/Spigot servers"

  apply(plugin = "java-library")
  apply(plugin = "maven-publish")
  apply(plugin = "signing")
  apply(plugin = "com.gradleup.nmcp")

  repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
      content {
        includeGroup("org.spigotmc")
      }
    }
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

  tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs = mutableListOf("-Xlint:-deprecation,-unchecked")
  }
}

nmcpAggregation {
  centralPortal {
    username = providers.gradleProperty("centralUsername")
    password = providers.gradleProperty("centralPassword")
    publishingType = "USER_MANAGED"
  }
}

dependencies {
  nmcpAggregation(project(":scoreboard-library-api"))
  nmcpAggregation(project(":scoreboard-library-extra-kotlin"))
  nmcpAggregation(project(":scoreboard-library-implementation"))
}
