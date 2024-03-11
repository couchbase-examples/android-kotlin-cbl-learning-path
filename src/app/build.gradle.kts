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
    val kotlinVersion:String by rootProject.extra
    val coreKtx:String by rootProject.extra
    val composeVersion:String by rootProject.extra
    val composeUiVersion:String by rootProject.extra
    val activityComposeVersion:String by rootProject.extra
    val lifestyleRuntimeKtVersion:String by rootProject.extra
    val koinVersion:String by rootProject.extra
    val androidMaterialDesignVersion:String by rootProject.extra
    val androidxNavigationComposeVersion:String by rootProject.extra
    val googleAccompanistVersion:String by rootProject.extra
    val kotlinxSerializationJson:String by rootProject.extra
    val constraintsComposeVersion:String by rootProject.extra
    val annotationExperimentalVersion:String by rootProject.extra
    val coroutinesTestsVersion:String by rootProject.extra

    //couchbase
    val couchbaseLiteVersion:String by rootProject.extra
    //core serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJson")

    //core compose
    implementation("androidx.core:core-ktx:$coreKtx")
    implementation("androidx.compose.ui:ui:$composeUiVersion")

    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:$composeUiVersion")

    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:$composeUiVersion")
    implementation("androidx.compose.foundation:foundation-layout:$composeUiVersion")

    // animation
    implementation("androidx.compose.animation:animation:$composeUiVersion")

    // Material design and icons
    implementation("androidx.compose.material:material:$composeUiVersion")
    implementation("androidx.compose.material:material-icons-core:$composeUiVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeUiVersion")
    implementation("com.google.android.material:material:$androidMaterialDesignVersion")

    //lifecycle and integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifestyleRuntimeKtVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifestyleRuntimeKtVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifestyleRuntimeKtVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifestyleRuntimeKtVersion")

    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:$composeUiVersion")

    // Integration with activities
    implementation("androidx.activity:activity-compose:$activityComposeVersion")

    // Integration with constraints
    implementation("androidx.constraintlayout:constraintlayout-compose:$constraintsComposeVersion")

    // navigation
    implementation("androidx.navigation:navigation-compose:$androidxNavigationComposeVersion")

    //fix for android versions older than 9 that won't load images https://github.com/google/accompanist
    implementation("com.google.accompanist:accompanist-drawablepainter:0.34.0")
    implementation("com.google.accompanist:accompanist-insets:0.30.1")

    // Dependency injection
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")

    //couchbase lite for kotlin
    implementation("com.couchbase.lite:couchbase-lite-android-ktx:3.1.6")

    //required because some flow APIs are still experimental (Card's onclick and cblite flow)
    implementation("androidx.annotation:annotation-experimental:$annotationExperimentalVersion")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeUiVersion")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestUtil("androidx.test:orchestrator:1.4.2")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeUiVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeUiVersion")
}