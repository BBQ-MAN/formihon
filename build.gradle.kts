plugins {
    id("com.android.application") version "8.1.1"
    kotlin("android") version "1.9.10"
}

android {
    namespace = "eu.kanade.tachiyomi.extension.ko.kmana"
    compileSdk = 34

    defaultConfig {
        applicationId = "eu.kanade.tachiyomi.extension.ko.kmana"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    
    // Core Tachiyomi API stubs (Provided by the host app)
    compileOnly("eu.kanade.tachiyomi.lib:core:1.4")
    compileOnly("eu.kanade.tachiyomi:extension-api:1.4")

    // RxJava & Coroutines
    compileOnly("io.reactivex:rxjava:1.3.8")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // OkHttp & Jsoup
    compileOnly("com.squareup.okhttp3:okhttp:4.12.0")
    compileOnly("org.jsoup:jsoup:1.17.2")
}
