plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)

}


kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
}