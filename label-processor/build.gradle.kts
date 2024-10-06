plugins {
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.20-1.0.25") // Adjust the version if necessary
    implementation(project(":label-annotations"))
}

