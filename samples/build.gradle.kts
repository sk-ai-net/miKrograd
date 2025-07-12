plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
}


group = "org.mikrograd.samples"

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
            implementation(project(":miKrograd"))
        }

        commonTest.dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        val jvmMain by getting {
            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/kotlin")
            dependencies {
                implementation(project(":miKrograd-annotations"))
            }
        }



        jvmTest.dependencies {
            implementation(kotlin("test-junit"))
        }
    }
}

dependencies {
    //    add("kspCommonMainMetadata", project(":test-processor"))
    add("kspJvm", project(":miKrograd-processor"))
}

ksp {
    arg("ksp.verbose", "true")
}
