plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
  //id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

repositories {
  maven("https://repo.viaversion.com")
}

dependencies {
  api(project(":scoreboard-library-api"))
  compileOnly(libs.spigotApi)

  compileOnly("com.viaversion:viaversion-api:5.7.1")
  compileOnly("io.netty:netty-buffer:4.2.10.Final")
  compileOnly("io.netty:netty-handler:4.2.10.Final")
}
