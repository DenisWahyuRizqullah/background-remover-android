// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        // MENGIKUTI DOKUMENTASI: Mendaftarkan classpath plugin compiler
        classpath("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:2.3.21")
    }
}

plugins {
    // Plugin Anda yang lain tetap dibiarkan
    alias(libs.plugins.android.application) apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}
