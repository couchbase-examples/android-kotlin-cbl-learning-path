buildscript {
    //Gradle docs on Extra properties
    //https://docs.gradle.org/current/userguide/kotlin_dsl.html#extra_properties
    val kotlinVersion by extra("1.9.22")
    val coreKtx by extra("1.12.0")
    val composeVersion by extra("1.3.1")
    val composeUiVersion by extra("1.6.1")
    val activityComposeVersion by extra("1.8.2")
    val lifestyleRuntimeKtVersion by extra("2.7.0")
    val koinVersion by extra("3.5.3")
    val androidMaterialDesignVersion by extra("1.11.0")
    val androidxNavigationComposeVersion by extra("2.5.0")
    val googleAccompanistVersion by extra("0.23.1")
    val kotlinxSerializationJson by extra("1.6.2")
    val constraintsComposeVersion by extra("1.0.1")
    val annotationExperimentalVersion by extra("1.4.0")
    val coroutinesTestsVersion by extra("1.6.3")

    //couchbase
    val couchbaseLiteVersion by extra("3.1.3")


    repositories {
        maven(url = "https://mobile.maven.couchbase.com/maven2/dev/")
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:${kotlinVersion}")
    }
} // Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.android.application") version "8.3.0" apply false
    id("com.android.library") version "8.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.22" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}