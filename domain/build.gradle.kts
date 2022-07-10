plugins {
    id("org.jetbrains.kotlin.plugin.jpa")
    id("org.jetbrains.kotlin.plugin.spring")
}

dependencies {
    val querydslAptVersion: String by project
    val hibernateJpaVersion: String by project
    val hibernateTypesVersion: String by project

//    implementation("com.vladmihalcea:hibernate-types-55:$hibernateTypesVersion")
//    implementation("com.querydsl:querydsl-jpa")

//    kapt("com.querydsl:querydsl-apt:$querydslAptVersion")
//    kapt("org.hibernate.javax.persistence:hibernate-jpa-2.1-api:$hibernateJpaVersion")

//    api("org.springframework.boot:spring-boot-starter-data-jpa")

//    runtimeOnly("com.h2database:h2")
}

//tasks {
//    noArg {
//        annotation("javax.persistence.Entity")
//    }
//}
