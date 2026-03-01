rootProject.name = "scoreboard-library"

include(":api")
include(":implementation")
include(":extra-kotlin")
include(":test-plugin")

val modulePrefix = rootProject.name
rootProject.children.forEach {
  it.name = "$modulePrefix-${it.name}"
}
