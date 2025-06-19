plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.teksta.projectparent.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.teksta.projectparent.android"
        minSdk = 33
        targetSdk = 35
        versionCode = 2
        versionName = "2.0.0"
    }
    buildFeatures {
        compose = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.sharedFramework)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material)
    debugImplementation(libs.compose.ui.tooling)

    // The BOM makes sure that all of your Compose libraries are on the same, compatible version.
    implementation(platform("androidx.compose:compose-bom:2025.05.01"))

    // Add the specific Compose libraries you need (no versions needed due to BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.10.1")

    // NEW: Kotlinx Datetime library for multiplatform date/time handling
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")

    // NEW: Add this dependency for the extended Material icons
    implementation("androidx.compose.material:material-icons-extended")

    // NEW: Add this dependency for Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.9.0")

    implementation("androidx.compose.material3:material3:1.3.2") // Or latest M3 version

    implementation("com.russhwolf:multiplatform-settings-no-arg:1.3.0")


    implementation("androidx.compose.foundation:foundation:1.8.2") // Ensure foundation is up to date for pager

    // Coil for image loading in Compose
    implementation("io.coil-kt:coil-compose:2.7.0") // Or the latest version of Coil

    // This is for viewing @Preview composables in Android Studio
    debugImplementation("androidx.compose.ui:ui-tooling")

    // AndroidX Core KTX - Provides ContextCompat and other utilities
    implementation("androidx.core:core-ktx:1.13.1") // Or the latest stable version
}