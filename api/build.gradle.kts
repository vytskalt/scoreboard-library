dependencies {
  compileOnlyApi(libs.bundles.adventure)
  compileOnly(libs.spigotApi)
}

publishing {
  publications.create<MavenPublication>("maven")
}
