import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jvmTarget = "17"
val micronautVersion="4.5.0"
val micrometerRegistryPrometheusVersion = "1.9.1"
val junitJupiterVersion = "5.9.0"
val logbackClassicVersion = "1.4.12"
val logbackEncoderVersion = "7.3"
val postgresqlVersion= "42.7.2"
val tcVersion= "1.17.6"
val mockkVersion = "1.13.4"
val kotestVersion = "5.5.5"
val rapidsRiversVersion = "202401101532"
val grunndataDtoVersion = "202406051156"
val leaderElectionVersion = "202405291312"

group = "no.nav.hm"
version = properties["version"] ?: "local-build"

plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("kapt") version "1.9.20"
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("io.micronaut.application") version "4.3.8"
}

configurations.all {
    resolutionStrategy {
        failOnChangingVersions()
    }
}

dependencies {

    api("ch.qos.logback:logback-classic:$logbackClassicVersion")
    api("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")


    runtimeOnly("org.yaml:snakeyaml")
    implementation("io.micronaut:micronaut-jackson-databind")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    // security
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.security:micronaut-security-annotations")
    kapt("io.micronaut.security:micronaut-security-annotations")

    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("io.micronaut.sql:micronaut-jdbc-hikari")

    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("org.flywaydb:flyway-database-postgresql:10.6.0")

    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut.micrometer:micronaut-micrometer-core")
    implementation("io.micronaut.micrometer:micronaut-micrometer-registry-prometheus")
    implementation("io.micronaut:micronaut-management")

    // Rapids and Rivers
    implementation("com.github.navikt:hm-rapids-and-rivers-v2-core:$rapidsRiversVersion")
    implementation("com.github.navikt:hm-rapids-and-rivers-v2-micronaut:$rapidsRiversVersion")
    implementation("no.nav.hm.grunndata:hm-grunndata-rapid-dto:$grunndataDtoVersion")

    // OpenApi
    implementation("io.micronaut.openapi:micronaut-openapi")
    implementation("io.swagger.core.v3:swagger-annotations")

    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut.openapi:micronaut-openapi")

    implementation("io.micronaut.cache:micronaut-cache-caffeine")

    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.micronaut.test:micronaut-test-kotest5")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.testcontainers:postgresql:${tcVersion}")

    // micronaut-leaderelection
    implementation("com.github.navikt:hm-micronaut-leaderelection:$leaderElectionVersion")
}

micronaut {
    version.set(micronautVersion)
    testRuntime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
    }
}

application {
    mainClass.set("no.nav.hm.grunndata.importapi.Application")
}

java {
    sourceCompatibility = JavaVersion.toVersion(jvmTarget)
    targetCompatibility = JavaVersion.toVersion(jvmTarget)
    withSourcesJar()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jvmTarget
}

tasks.named<KotlinCompile>("compileTestKotlin") {
    kotlinOptions.jvmTarget = jvmTarget
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("skipped", "failed")
        showExceptions = true
        showStackTraces = true
        showCauses = true
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "8.5"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    maven("https://packages.confluent.io/maven/")

}

