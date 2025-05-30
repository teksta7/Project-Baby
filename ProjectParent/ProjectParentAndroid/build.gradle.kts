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
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))

    // Add the specific Compose libraries you need (no versions needed due to BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.9.0")

    // NEW: Kotlinx Datetime library for multiplatform date/time handling
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    // NEW: Add this dependency for the extended Material icons
    implementation("androidx.compose.material:material-icons-extended")

    // NEW: Add this dependency for Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("androidx.compose.material3:material3:1.2.1") // Or latest M3 version

    // This is for viewing @Preview composables in Android Studio
    debugImplementation("androidx.compose.ui:ui-tooling")
}