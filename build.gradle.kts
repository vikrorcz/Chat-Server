val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.7.0"
    //kotlin("plugin.serialization") version "1.7.0"
    //flyway()
}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("io.ktor:ktor-server-auth:$ktor_version")

    implementation("org.postgresql:postgresql:42.3.0")
    implementation("com.zaxxer:HikariCP:4.0.3")//4.0.3

    implementation("org.flywaydb:flyway-core:6.5.2")

    implementation("org.jetbrains.exposed", "exposed-core", "0.38.1")//0.38.1
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.38.1")//0.38.1
    //implementation("org.jetbrains.exposed", "exposed-dao", "0.38.1")

    // password hashing
    implementation("org.mindrot:jbcrypt:0.4")





    //implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")



    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

