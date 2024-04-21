plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "meteor"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://raw.githubusercontent.com/MeteorLite/hosting/main/repo/") }
}

dependencies {
    with(projects) {
        implementation(annotations)
        implementation(api)
        implementation(apiRs)
        implementation(logger)
    }

    with(libs) {
        annotationProcessor(lombok)
        implementation(annotations)
        implementation(asm)
        implementation(asm.util)
        implementation(fernflower)
        implementation(gson)
        implementation(guava)
        compileOnly(lombok)
    }
}

tasks.test {
    useJUnitPlatform()
}

java {
    disableAutoTargetJvm()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        apiVersion = "1.8"
        languageVersion = "1.8"
        jvmTarget = "17"
    }
}