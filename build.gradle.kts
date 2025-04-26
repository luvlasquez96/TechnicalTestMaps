// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val navVersion = "2.5.3"
    val kotlin_version = "1.9.0"
    val compose_version = "1.4.0"
    val hilt_version = "2.47"

    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}