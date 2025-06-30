import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("com.squareup.sqldelight") version "1.5.5"
    //kotlin("native.cocoapods")
    //id("org.jetbrains.compose.resources") version "1.6.10"
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()

    // Configure framework for iOS targets
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach {
//        it.binaries.framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting

//        val iosX64Main by getting
//        val iosArm64Main by getting
//        val iosSimulatorArm64Main by getting
//
//        // Create iosMain and set it as the parent of all iOS targets
//        val iosMain by creating {
//            dependsOn(commonMain)
//        }
//
//        // Explicitly set iOS dependencies since default template is disabled
//        iosX64Main.dependsOn(iosMain)
//        iosArm64Main.dependsOn(iosMain)
//        iosSimulatorArm64Main.dependsOn(iosMain)

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation("com.squareup.sqldelight:android-driver:1.5.5")
                implementation("com.google.android.material:material:1.12.0") // Or the latest version
                implementation("com.airbnb.android:lottie-compose:6.3.0")
            }
        }
//        iosMain.dependencies {
//            implementation("com.squareup.sqldelight:native-driver:1.5.5")
//        }
//        commonMain {
//            resources.srcDir("src/commonMain/composeResources")
//        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("com.squareup.sqldelight:runtime:1.5.5")
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.3.0")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.6.10")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
    
//    cocoapods {
//        summary = "Some description for the Shared Module"
//        homepage = "Link to the Shared Module homepage"
//        ios.deploymentTarget = "17.0"
//        podfile = project.file("../iosApp/Podfile")
//        //version = "1"
//
//        framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//    }
}

android {
    namespace = "com.teksta.projectparent"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].assets.srcDirs("src/androidMain/assets")

    defaultConfig {
        applicationId = "com.teksta.projectparent"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

sqldelight {
    database("AppDatabase") {
        packageName = "com.teksta.projectparent.db"
    }
}


repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
    maven("https://maven.pkg.github.com/ajalt/mp-date-picker") {
        credentials {
            username = project.findProperty("gpr.user") as String? ?: ""
            password = project.findProperty("gpr.key") as String? ?: ""
        }
    }
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

