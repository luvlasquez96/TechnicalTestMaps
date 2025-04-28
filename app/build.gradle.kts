plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.technicaltestmaps"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.technicaltestmaps"
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
}

val nav_version = "2.5.3"
val room_version = "2.5.1"
val coroutines_version = "1.6.4"
val lifecycle_version = "2.5.1"

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt
    implementation ("com.google.dagger:hilt-android:2.47")
    implementation ("androidx.hilt:hilt-navigation-fragment:1.0.0")
    testImplementation ("com.google.dagger:hilt-android-testing:2.47")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")
    implementation ("com.jakewharton.timber:timber:5.0.1")
    kapt ("com.google.dagger:hilt-compiler:2.47")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")

    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // Coil
    implementation ("io.coil-kt:coil-compose:2.2.2")
    implementation ("com.squareup.picasso:picasso:2.5.2")

    // Gson
    implementation ("com.google.code.gson:gson:2.10.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Mapbox
    implementation("com.mapbox.maps:android:11.11.0")
    implementation("com.mapbox.extension:maps-compose:11.11.0")
    implementation("com.google.accompanist:accompanist-permissions:0.28.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Google Fonts
    implementation ("androidx.compose.ui:ui-text-google-fonts:1.7.3")

    // Para hacer HTTP simple
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

// Para manejo de JSON de Mapbox (GeoJSON)
    implementation ("com.mapbox.geojson:mapbox-geojson:6.3.0")

    // Test
    // Turbine for Flow testing
    testImplementation("app.cash.turbine:turbine:1.0.0")

    // JUnit 4
    testImplementation("junit:junit:4.13.2")

    // Kotlin Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // Truth assertion library
    testImplementation("com.google.truth:truth:1.1.3")

    // MockK for mocking
    testImplementation("io.mockk:mockk:1.13.4")

    // Android specific test dependencies
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}