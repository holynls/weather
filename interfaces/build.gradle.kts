import com.google.cloud.tools.jib.gradle.JibExtension

tasks {
    configure<JibExtension> {
        from {
            image = "openjdk:17"
        }

        to {
            image = "weather-api"
        }

        container {
            val memory = "2g"

            jvmFlags = listOf(
                "-server",
                "-Xms$memory",
                "-Xmx$memory",
                "-Djava.net.preferIPv4Stack=true",
                "-Dlog4j2.formatMsgNoLookups=true",
                "-XX:+UseG1GC",
                "-XX:MaxGCPauseMillis=200",
                "-XX:G1HeapRegionSize=8m",
                "-XX:+ParallelRefProcEnabled",
                "-XX:-ResizePLAB"
                // "-XX:+PrintClassHistogramAfterFullGC",
                // "-XX:+PrintClassHistogramBeforeFullGC"
            )
            environment = mapOf(
                "SPRING_PROFILES_ACTIVE" to "development"
            )
            ports = listOf("8080")
        }
    }
}

dependencies {
    val springDocVersion: String by project

    implementation(project(":domain"))

    implementation("org.springdoc:springdoc-openapi-ui:$springDocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springDocVersion")

    implementation("io.github.resilience4j:resilience4j-timelimiter:1.7.0")
}
