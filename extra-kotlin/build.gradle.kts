import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
  alias(libs.plugins.kotlin)
}

kotlin {
  explicitApi()
}

dependencies {
  api(project(":scoreboard-library-api"))
  testImplementation(kotlin("test"))
  compileOnly(libs.spigotApi)
}

tasks.withType<KotlinJvmCompile>().configureEach {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_1_8
  }
}
