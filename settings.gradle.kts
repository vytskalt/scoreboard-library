rootProject.name = "scoreboard-library"

include(":api")
include(":implementation")
include(":extra-kotlin")
include(":test-plugin")

val modulePrefix = rootProject.name
rootProject.children.forEach {
  it.name = "$modulePrefix-${it.name}"
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
