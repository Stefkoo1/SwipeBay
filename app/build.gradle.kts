plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    kotlin("kapt") // Für Room Annotation Processing
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.swipebay"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.swipebay"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // oder was in deiner BOM passt
    }
}

dependencies {
    // --- Compose Core ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth.ktx)

    // --- Jetpack Compose Dev Tools ---
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // --- Navigation ---
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // --- ViewModel for Compose ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // --- Room (DB) ---
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // --- Coroutines ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // --- Coil (Images) ---
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.accompanist:accompanist-flowlayout:0.30.1")
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("com.google.firebase:firebase-storage")

}

