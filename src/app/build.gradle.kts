plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}
android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.couchbase.kotlin.learningpath"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // The following argument makes the Android Test Orchestrator run its
        // "pm clear" command after each test invocation. This command ensures
        // that the app's state is completely cleared between tests.
        testInstrumentationRunnerArguments(mapOf("clearPackageData" to "true"))

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        // We use a bundled debug keystore, to allow debug builds from CI to be upgradable
        getByName("debug") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }

    packagingOptions {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
    namespace = "com.couchbase.learningpath"
}

dependencies {

    //Gradle docs on Extra properties
    //https://docs.gradle.org/current/userguide/kotlin_dsl.html#extra_properties
    val kotlin_version:String by rootProject.extra
    val core_ktx:String by rootProject.extra
    val compose_version:String by rootProject.extra
    val compose_ui_version:String by rootProject.extra
    val activity_compose_version:String by rootProject.extra
    val lifecyle_runtime_ktx_version:String by rootProject.extra
    val koin_version:String by rootProject.extra
    val android_materialdesign_version:String by rootProject.extra
    val androidx_navigation_compose_version:String by rootProject.extra
    val google_accompanist_version:String by rootProject.extra
    val kotlinx_serialization_json:String by rootProject.extra
    val constraints_compose_version:String by rootProject.extra
    val annotation_experimental_version:String by rootProject.extra
    val coroutines_tests_version:String by rootProject.extra

    //couchbase
    val couchbase_lite_version:String by rootProject.extra
    //core serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_json")

    //core compose
    implementation("androidx.core:core-ktx:$core_ktx")
    implementation("androidx.compose.ui:ui:$compose_ui_version")

    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:$compose_ui_version")

    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:$compose_ui_version")
    implementation("androidx.compose.foundation:foundation-layout:$compose_ui_version")

    // animation
    implementation("androidx.compose.animation:animation:$compose_ui_version")

    // Material design and icons
    implementation("androidx.compose.material:material:$compose_ui_version")
    implementation("androidx.compose.material:material-icons-core:$compose_ui_version")
    implementation("androidx.compose.material:material-icons-extended:$compose_ui_version")
    implementation("com.google.android.material:material:$android_materialdesign_version")

    //lifecycle and integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecyle_runtime_ktx_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecyle_runtime_ktx_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecyle_runtime_ktx_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecyle_runtime_ktx_version")

    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:$compose_ui_version")

    // Integration with activities
    implementation("androidx.activity:activity-compose:$activity_compose_version")

    // Integration with constraints
    implementation("androidx.constraintlayout:constraintlayout-compose:$constraints_compose_version")

    // navigation
    implementation("androidx.navigation:navigation-compose:$androidx_navigation_compose_version")

    //fix for android versions older than 9 that won't load images https://github.com/google/accompanist
    implementation("com.google.accompanist:accompanist-drawablepainter:0.34.0")
    implementation("com.google.accompanist:accompanist-insets:0.30.1")

    // Dependency injection
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-android:$koin_version")
    implementation("io.insert-koin:koin-androidx-compose:$koin_version")

    //couchbase lite for kotlin
    implementation("com.couchbase.lite:couchbase-lite-android-ktx:$couchbase_lite_version")

    //required because some flow APIs are still experimental (Card's onclick and cblite flow)
    implementation("androidx.annotation:annotation-experimental:$annotation_experimental_version")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_ui_version")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestUtil("androidx.test:orchestrator:1.4.2")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_ui_version")
}