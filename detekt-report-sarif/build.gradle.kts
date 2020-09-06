dependencies {
    implementation(project(":detekt-api"))
    implementation("com.eclipsesource.minimal-json:minimal-json:0.9.5")
    testImplementation(testFixtures(project(":detekt-api")))
}
