plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.audiovideoplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.audiovideoplayer"
        minSdk = 24 // Increased minSdk to support Media3 features better
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Enable minification for release
            isShrinkResources = true // Remove unused resources
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
        kotlinCompilerExtensionVersion = "1.5.1" // or latest
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // UI

    implementation("androidx.compose.ui:ui:1.7.8") // Or latest
    implementation("androidx.compose.material3:material3:1.3.1") // Or latest
    implementation("androidx.navigation:navigation-compose:2.8.8") // Or latest
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8") // Or latest
    implementation("androidx.compose.runtime:runtime-livedata:1.7.8") // Add livedata support for compose
    debugImplementation("androidx.compose.ui:ui-tooling:1.7.8") // Or latest
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.8") // Or latest

    // ViewModel and Lifecycle

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7") // Or latest
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7") // Or latest
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7") // Or latest

    // ExoPlayer (Media3)

    implementation("androidx.media3:media3-exoplayer:1.5.1") // Or latest
    implementation("androidx.media3:media3-ui:1.5.1") // Or latest
    implementation("androidx.media3:media3-exoplayer-dash:1.5.1") // Add dash support if needed
    implementation("androidx.media3:media3-exoplayer-hls:1.5.1") // Add hls support if needed
    implementation("androidx.media3:media3-exoplayer-rtsp:1.5.1") // Add rtsp support if needed

    // Permissions Handling

    implementation("com.google.accompanist:accompanist-permissions:0.31.6-rc") // Or latest

    // System UI Controller
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.6-rc") // Or latest

    // Coil

    implementation("io.coil-kt:coil-compose:2.2.2")





    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}