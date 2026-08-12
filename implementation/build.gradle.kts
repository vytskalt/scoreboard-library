plugins {
  //id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

repositories {
  maven("https://repo.viaversion.com")
}

dependencies {
  api(project(":scoreboard-library-api"))
  compileOnly(libs.spigotApi)
  compileOnly(libs.viaversionApi)
  compileOnly(libs.bundles.netty)
}

publishing {
  publications.create<MavenPublication>("maven")
}
