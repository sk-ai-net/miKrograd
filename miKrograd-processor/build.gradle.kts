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
                implementation(libs.kotlinpoet) // Use version from libs.versions.toml
                implementation(libs.kotlinpoet.ksp) // Required for KSP integration
                implementation(libs.ksp.api)
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(libs.kotlin.compile.testing)
                implementation(libs.kotlin.compile.testing.ksp)
                implementation(project(":miKrograd-annotations"))
            }
            kotlin.srcDir("src/test/kotlin")
            resources.srcDir("src/test/resources")
        }
    }
}
