plugins {
    kotlin("multiplatform")
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":miKrograd-annotations"))
                implementation("com.squareup:kotlinpoet:1.14.2") // Adjust version as necessary
                implementation("com.squareup:kotlinpoet-ksp:1.14.2") // Required for KSP integration
                implementation("com.google.devtools.ksp:symbol-processing-api:2.1.0-1.0.29")
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
    }
}

