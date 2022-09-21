# Learn Couchbase Lite with Kotlin and Jetpack Compose

In this learning path you will be reviewing an Android Application written in Kotlin and JetPack Compose that uses the Couchbase Lite Android SDK for Kotlin. You will learn how to get and insert documents using the key-value engine, query the database using the QueryBuilder engine or SQL++, and learn how to sync information between your mobile app and either a Couchbase Server using Sync Gateway or using peer-to-peer technology.

Full documentation can be found on the [Couchbase Developer Portal](https://developer.couchbase.com/learn/android-kotlin).

## Prerequisites
To run this prebuilt project, you will need:
- Familiarity with building Android Apps with <a target="_blank" rel="noopener noreferrer" href="https://developer.android.com/kotlin">Kotlin</a>, <a target="_blank" rel="noopener noreferrer"  href="https://developer.android.com/jetpack/compose/mental-model">JetPack Compose</a>, and Android Studio 
- [Android Studio Chimpmuck or above](https://developer.android.com/studio)
- Android SDK installed and setup (> v.33.0.0)
- Android Build Tools (> v.33.0.0)
- Android device or emulator running API level 24 or above
- JDK 11 (now embedded into Android Studio 4+)

### Installing Couchbase Lite Framework

- src/build.gradle already contains the appropriate additions for downloading and utilizing the Android Couchbase Lite dependency module. However, in the future, to include Couchbase Lite support within an Android app add the following within the Module gradle file (src/app/build.gradle)

```bash
allprojects {
    repositories {
        ...

        maven {
            url "https://mobile.maven.couchbase.com/maven2/dev/"
        }
    }
}
``` 
 
Then add the following to the <a target="_blank" rel="noopener noreferrer" href="https://github.com/couchbase-examples/android-kotlin-cblite-inventory-standalone/blob/main/src/app/build.gradle">app/build.gradle</a> file.

```bash
dependencies {
    ...

    implementation "com.couchbase.lite:couchbase-lite-android-ktx:3.0.2"
}
```

For more information on installation, please see the [Couchbase Lite Documentation](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html).

## Demo Application 

### Overview

The demo application used in this learning path is based on auditing <a target="_blank" rel="noopener noreferrer" href="https://en.wikipedia.org/wiki/Inventory">inventory</a>  for a fictitious company.  Most companies have some inventory - from laptops to office supplies and from time to time must audit their stock.  For example, when a user's laptop breaks, they can send out a replacement from the inventory of spare laptops they have on hand. 

Users running the mobile app would log into the application to see the projects they are assigned to work on. Then, the user would look at the project to see which office location they need to travel to. Once at the site, they would inspect the number of items, tracking them in the mobile application.

### Architecture

The demo application uses <a target="_blank" rel="noopener noreferrer" href="https://developer.android.com/jetpack/guide">application architecture</a> concepts in developing modern Android applications recommended by the Android development team.  

<a target="_blank" rel="noopener noreferrer" href="https://insert-koin.io/">Koin</a>, the popular open-source Kotlin based injection library, is used to manage dependency inversion and injection.  Using Koin we can use JDK 11 versus Hilt or Dagger, which requires JDK 8.  

The application structure is a single Activity that uses <a target="_blank" rel="noopener noreferrer"  href="https://developer.android.com/jetpack/compose/mental-model">JetPack Compose</a> to render the multiple compose-based views.  In addition, the <a target="_blank" rel="noopener noreferrer" href="https://developer.android.com/jetpack/compose/navigation">Navigation Graph</a> is used to handle routing and navigation between various views.  

The Inventory Database is a custom class that manages the database state and lifecycle.  Querying and updating documents in the database is handled using the <a target="_blank" rel="noopener noreferrer" href="https://developer.android.com/jetpack/guide#data-layer">repository pattern</a>.  <a target="_blank" rel="noopener noreferrer" href="https://developer.android.com/jetpack/guide#domain-layer">ViewModels</a> will query or post updates to the repository and control the state of objects that the compose-based Views can use to display information. 

### Flow

The demo application starts with the InventoryApplication class, which inherits from the default Application class provided by Android.  <a target="_blank" rel="noopener noreferrer" href="https://insert-koin.io/docs/reference/koin-android/start">Koin</a> recommends this structure and is used to set up all the dependencies, including services, repositories, and ViewModels.  

MainActivity then runs the setContent method which set up lifecycle management and creates the navigation controller used to handle navigation in the application.  The InventoryNavGraph function handles routing between views and sets the start destination to the LoginView function.  The InventoryNavGraph passes which view the LoginView should route to if the user logs in successfully.

## Try it out

* Open src/build.gradle using Android Studio.
* Build and run the project.
* Verify that you see the login screen.
