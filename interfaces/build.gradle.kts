dependencies {
    val springDocVersion: String by project

    implementation(project(":domain"))

    implementation("org.springdoc:springdoc-openapi-ui:$springDocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springDocVersion")
}
