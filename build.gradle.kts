plugins {
    id("com.android.application") version "8.7.2"
    kotlin("android") version "2.0.21"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


}

dependencies {
    implementation(kotlin("stdlib"))
    
    // Core Tachiyomi API stubs
    compileOnly("com.github.keiyoushi:extensions-lib:6e0c96cea8")
    compileOnly("com.github.null2264.injekt:injekt-core:4135455a2a")

    // RxJava & Coroutines
    compileOnly("io.reactivex:rxjava:1.3.8")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // OkHttp & Jsoup
    compileOnly("com.squareup.okhttp3:okhttp:4.12.0")
    compileOnly("org.jsoup:jsoup:1.17.2")
}
