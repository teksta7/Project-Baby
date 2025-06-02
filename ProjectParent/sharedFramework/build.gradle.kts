import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "sharedFramework"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            // Coroutines Library for asynchronous programming
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

            // Multiplatform Settings for key-value storage
            // Use the 'no-arg' artifact to easily create instances in common code
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")

            // NEW: Kotlinx Datetime library for multiplatform date/time handling
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

            implementation("com.benasher44:uuid:0.8.2")

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.teksta.projectparent"
    compileSdk = 35
    defaultConfig {
        minSdk = 33
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation(libs.androidx.core)
}
