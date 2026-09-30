import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jvmTarget = "25"
val micronautVersion="5.2.0"
val logbackClassicVersion = "1.5.13"
val logbackEncoderVersion = "7.3"
val postgresqlVersion = "42.7.2"
val tcVersion = "2.0.1"
val mockkVersion = "1.13.4"
val kotestVersion = "5.5.5"
val rapidsRiversVersion = "202606190809"
val grunndataDtoVersion = "202609220836"
val leaderElectionVersion = "202606231046"

group = "no.nav.hm"
version = properties["version"] ?: "local-build"

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.3.21"
    id("org.jetbrains.kotlin.kapt") version "2.3.21"
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
    id("io.micronaut.application") version "5.0.2"
    id("io.micronaut.aot") version "5.0.2"
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
    implementation("tools.jackson.module:jackson-module-kotlin")

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
    testImplementation("org.testcontainers:testcontainers-postgresql:${tcVersion}")
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
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(jvmTarget))
}

tasks.named<KotlinCompile>("compileTestKotlin") {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(jvmTarget))
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
    gradleVersion = "9.3.1"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    maven("https://packages.confluent.io/maven/")
}
