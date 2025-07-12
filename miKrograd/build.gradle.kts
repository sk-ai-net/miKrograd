@file:Suppress("OPT_IN_USAGE")

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}


group = "org.mikrograd"

kotlin {

    compilerOptions {
        // Common compiler options applied to all Kotlin source sets
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xmulti-platform")
    }

    jvmToolchain(17)

    jvm()


    sourceSets {
        commonMain.dependencies {
            implementation(project(":miKrograd-annotations"))

        }

        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }


        jvmTest.dependencies {
            implementation(kotlin("test-junit"))
        }
    }
}
