buildscript {
    //Gradle docs on Extra properties
    //https://docs.gradle.org/current/userguide/kotlin_dsl.html#extra_properties
    val kotlin_version by extra("1.9.22")
    val core_ktx by extra("1.12.0")
    val compose_version by extra("1.3.1")
    val compose_ui_version by extra("1.6.1")
    val activity_compose_version by extra("1.8.2")
    val lifecyle_runtime_ktx_version by extra("2.7.0")
    val koin_version by extra("3.5.3")
    val android_materialdesign_version by extra("1.11.0")
    val androidx_navigation_compose_version by extra("2.5.0")
    val google_accompanist_version by extra("0.23.1")
    val kotlinx_serialization_json by extra("1.6.2")
    val constraints_compose_version by extra("1.0.1")
    val annotation_experimental_version by extra("1.4.0")
    val coroutines_tests_version by extra("1.6.3")

    //couchbase
    val couchbase_lite_version by extra("3.1.3")


    repositories {
        maven(url = "https://mobile.maven.couchbase.com/maven2/dev/")
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:${kotlin_version}")
    }
} // Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.22" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}