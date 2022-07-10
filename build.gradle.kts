plugins {
    jacoco
    kotlin("jvm")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.jpa")
    id("org.springframework.boot")
    id("org.jetbrains.kotlin.plugin.spring")
}

tasks {
    bootJar {
        enabled = false
    }
}

allprojects {
    group = "kr.doctornow"
    version = "1.0.0"

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.spring.io/release")
        maven("https://repo.spring.io/milestone/")
        maven("https://jitpack.io")
    }
}

subprojects {
    val kotlinVersion: String by project
    val coroutinesVersion: String by project
    val springBootVersion: String by project
    val jacocoVersion: String by project
    val arrowVersion: String by project
    val kotlinFakerVersion: String by project
    val mockitoKotlinVersion: String by project
    val kluentVersion: String by project
    val mockkVersion: String by project
    val junitJupiterVersion: String by project
    val logbackContribVersion: String by project
    val retrofitVersion: String by project
    val okHttpVersion: String by project
    val jacksonVersion: String by project

    apply(plugin = "jacoco")
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    tasks {
        withType<Test> {
            useJUnitPlatform()
            maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2)
        }

        getByName("test") {
            enabled = false
        }

        compileKotlin {
            kotlinOptions.jvmTarget = "17"
        }

        compileTestKotlin {
            kotlinOptions.jvmTarget = "17"
        }

        bootJar {
            enabled = false
        }

        jar {
            enabled = true
        }

        clean {
            delete("out/")
        }

        jacoco {
            toolVersion = jacocoVersion
        }

        val jacocoExcludes = listOf(
            "**/*RepositoryCustomImpl.class",
        )

        withType<JacocoReport> {
            executionData.setFrom("build/jacoco/unitTest.exec")
            reports {
                html.required.set(true)
            }
            afterEvaluate {
                classDirectories.setFrom(
                    files(
                        classDirectories.files.map {
                            fileTree("dir" to it, "excludes" to jacocoExcludes)
                        }
                    )
                )
            }
            doLast {
                val reportDir = reporting.file("jacoco/test/html")
                println("See report at: file://${reportDir.toURI().path}index.html")
            }
        }

        withType<JacocoCoverageVerification> {
            violationRules {
                rule {
                    limit {
                        counter = "BRANCH"
                        value = "COVEREDRATIO"
                        minimum = 0.80.toBigDecimal()
                    }
                }
            }
            afterEvaluate {
                classDirectories.setFrom(
                    files(
                        classDirectories.files.map {
                            fileTree("dir" to it, "excludes" to jacocoExcludes)
                        }
                    )
                )
            }
        }

        dependencies {
            implementation(kotlin("stdlib"))
            implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")

            implementation(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
            implementation("org.springframework.boot:spring-boot-starter-web")
            implementation("org.springframework.boot:spring-boot-starter-aop")

            implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

            implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
            implementation("com.squareup.retrofit2:converter-jackson:$retrofitVersion")
            implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")

            implementation("ch.qos.logback.contrib:logback-jackson:$logbackContribVersion")
            implementation("ch.qos.logback.contrib:logback-json-classic:$logbackContribVersion")

            implementation("io.arrow-kt:arrow-core:$arrowVersion")

            runtimeOnly("org.springframework.boot:spring-boot-devtools")

            testImplementation("io.github.serpro69:kotlin-faker:$kotlinFakerVersion")
            testImplementation("org.springframework.boot:spring-boot-starter-test") {
                exclude(group = "junit")
                exclude(group = "org.junit.vintage")
            }
            testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
            testImplementation("org.amshove.kluent:kluent:$kluentVersion")
            testImplementation("io.mockk:mockk:$mockkVersion")
            testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
            testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
        }
    }
}
