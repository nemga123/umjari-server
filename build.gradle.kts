import com.adarshr.gradle.testlogger.theme.ThemeType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
    id("com.adarshr.test-logger") version "3.2.0"
    id("jacoco")
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.20"
}

group = "com.umjari"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-mail:3.0.2")
    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.4")
    implementation("org.thymeleaf:thymeleaf-spring5:3.0.15.RELEASE")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

testlogger {
    // pick a theme - mocha, standard, plain, mocha-parallel, standard-parallel or plain-parallel
    theme = ThemeType.STANDARD

    // set to false to disable detailed failure logs
    showExceptions = true

    // set to false to hide stack traces
    showStackTraces = true

    // set to true to remove any filtering applied to stack traces
    showFullStackTraces = false

    // set to false to hide exception causes
    showCauses = true

    // set threshold in milliseconds to highlight slow tests
    slowThreshold = 2000

    // displays a breakdown of passes, failures and skips along with total duration
    showSummary = true

    // set to true to see simple class names
    showSimpleNames = false

    // set to false to hide passed tests
    showPassed = true

    // set to false to hide skipped tests
    showSkipped = true

    // set to false to hide failed tests
    showFailed = true

    // enable to see standard out and error streams inline with the test results
    showStandardStreams = false

    // set to false to hide passed standard out and error streams
    showPassedStandardStreams = true

    // set to false to hide skipped standard out and error streams
    showSkippedStandardStreams = true

    // set to false to hide failed standard out and error streams
    showFailedStandardStreams = true
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

jacoco {
    toolVersion = "0.8.8"
    reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}
