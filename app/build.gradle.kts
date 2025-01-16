import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.into.websoso"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.into.websoso"
        minSdk = 30
        targetSdk = 34
        versionCode = libs.versions.versionCode
            .get()
            .toInt()
        versionName = libs.versions.versionName.get()

        buildConfigField("String", "S3_BASE_URL", gradleLocalProperties(rootDir).getProperty("s3.url"))
        buildConfigField("String", "KAKAO_APP_KEY", gradleLocalProperties(rootDir).getProperty("kakao.app.key"))
        buildConfigField("String", "AMPLITUDE_KEY", gradleLocalProperties(rootDir).getProperty("amplitude.key"))

        manifestPlaceholders["kakaoAppKey"] = gradleLocalProperties(rootDir)
            .getProperty("kakao.app.key")
            .replace("\"", "")
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
            isMinifyEnabled = true
            isShrinkResources = true
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.viewpager2)
    implementation(libs.fragment.ktx)
    implementation(libs.lifecycle.extensions)
    implementation(libs.datastore.preferences)
    implementation(libs.security.crypto)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.coroutines)

    implementation(libs.coil)
    implementation(libs.coil.gif)
    implementation(libs.coil.svg)
    implementation(libs.coil.transformers)

    implementation(libs.dots.indicator)
    implementation(libs.lottie)
    implementation(libs.pull.to.refresh)

    implementation(libs.kakao)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation(libs.amplitude)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.bom)

    implementation(libs.bundles.compose)
}
