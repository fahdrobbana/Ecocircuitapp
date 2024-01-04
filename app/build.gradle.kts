plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

}

android {
    namespace = "tn.esprit.ecocircuitapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "tn.esprit.ecocircuitapp"
        minSdk = 24
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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
// mapBox

    implementation ("com.mapbox.maps:android:10.16.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.mapbox.search:autofill:1.0.0-rc.6")
    implementation ("com.mapbox.search:discover:1.0.0-rc.6")
    implementation ("com.mapbox.search:place-autocomplete:1.0.0-rc.6")
    implementation ("com.mapbox.search:offline:1.0.0-rc.6")
    implementation ("com.mapbox.search:mapbox-search-android:1.0.0-rc.6")
    implementation ("com.mapbox.search:mapbox-search-android-ui:1.0.0-rc.6")




    implementation("com.mapbox.mapboxsdk:mapbox-android-plugin-annotation-v9:0.9.0") {
        exclude(group = "com.mapbox.mapboxsdk", module = "mapbox-android-core")
    }

    implementation ("com.mapbox.navigation:android:2.17.4")
    implementation ("com.mapbox.navigation:core:2.17.4")
    implementation ("com.mapbox.navigation:ui-dropin:2.17.4")


    // Mapbox Java SDK (includes services like Directions, Geocoding, and Map Matching)
    implementation ("com.mapbox.mapboxsdk:mapbox-sdk-services:6.14.0")


    implementation ("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")




    implementation ("com.mapbox.navigation:core:2.0.0")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")


    implementation("androidx.room:room-runtime:2.5.0")




    annotationProcessor("androidx.room:room-compiler:2.5.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("androidx.appcompat:appcompat:1.4.0")


    // for adding recyclerview
    implementation ("androidx.recyclerview:recyclerview:1.2.0")


    // for adding cardview
    implementation ("androidx.cardview:cardview:1.0.0")
    // logging interceptor for retrofit2
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")




    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-basement:18.2.0")
    implementation ("com.auth0.android:jwtdecode:2.0.2")
    implementation ("androidx.activity:activity-ktx:1.3.1")
    val activity_version = "1.8.1"
    implementation("androidx.activity:activity-ktx:$activity_version")

}