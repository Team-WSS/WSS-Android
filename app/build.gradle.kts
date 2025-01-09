import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.into.websoso"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.into.websoso"
        minSdk = 30
        targetSdk = 34
        versionCode = 10008
        versionName = "1.0.8"

        buildConfigField("String", "S3_BASE_URL", gradleLocalProperties(rootDir).getProperty("s3.url"))
        buildConfigField("String", "KAKAO_APP_KEY", gradleLocalProperties(rootDir).getProperty("kakao.app.key"))
        buildConfigField("String", "AMPLITUDE_KEY", gradleLocalProperties(rootDir).getProperty("amplitude.key"))

        manifestPlaceholders["kakaoAppKey"] = gradleLocalProperties(rootDir)
            .getProperty("kakao.app.key").replace("\"", "")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", gradleLocalProperties(rootDir).getProperty("debug.base.url"))
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", gradleLocalProperties(rootDir).getProperty("release.base.url"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.compose.ui:ui-android:1.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // Okhttp3
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // fragment-ktx
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // viewModel
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // coil
    implementation("io.coil-kt:coil:2.7.0")
    implementation("io.coil-kt:coil-gif:2.7.0")
    implementation("io.coil-kt:coil-svg:2.7.0")
    implementation("jp.wasabeef.transformers:coil:1.0.6")
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Pager Dots Indicator
    implementation("com.tbuonomo:dotsindicator:5.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    // lottie
    implementation("com.airbnb.android:lottie:5.0.2")

    // SwipeRefreshLayout
    implementation("com.github.SimformSolutionsPvtLtd:SSPullToRefresh:1.5.2")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // KAKAO
    implementation("com.kakao.sdk:v2-user:2.15.0")

    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Amplitude
    implementation("com.amplitude:analytics-android:1.+")

    // Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.ui:ui-tooling")
}
