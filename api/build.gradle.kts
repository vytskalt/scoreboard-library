dependencies {
  compileOnlyApi(libs.bundles.adventure)
  compileOnly(libs.spigotApi)
}

java {
  withJavadocJar()
}

tasks.javadoc {
  options {
    (this as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
  }
}

publishing {
  publications.create<MavenPublication>("maven")
}
