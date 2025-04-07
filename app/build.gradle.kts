import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("websoso.android.application")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.parcelize)
    id("websoso.android.hilt")
    id("websoso.android.compose")
    id("websoso.android.coroutines")
}

android {
    namespace = "com.into.websoso"

    defaultConfig {
        applicationId = "com.into.websoso"
        versionCode = libs.versions.versionCode
            .get()
            .toInt()
        versionName = libs.versions.versionName.get()

        buildConfigField(
            "String",
            "S3_BASE_URL",
            gradleLocalProperties(rootDir).getProperty("s3.url"),
        )
        buildConfigField(
            "String",
            "KAKAO_APP_KEY",
            gradleLocalProperties(rootDir).getProperty("kakao.app.key"),
        )
        buildConfigField(
            "String",
            "AMPLITUDE_KEY",
            gradleLocalProperties(rootDir).getProperty("amplitude.key"),
        )

        manifestPlaceholders["kakaoAppKey"] = gradleLocalProperties(rootDir)
            .getProperty("kakao.app.key")
            .replace("\"", "")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // 디버그 버전, 빌드 세팅(디버깅 가능 여부, 앱 네임, 아이콘, 패키지)
            isDebuggable = true
            applicationIdSuffix = ".debug"
            manifestPlaceholders.putAll(
                mapOf(
                    "appName" to "@string/app_name_debug",
                    "appIcon" to "@mipmap/ic_wss_logo_debug",
                    "roundIcon" to "@mipmap/ic_wss_logo_debug_round",
                ),
            )

            // 프로가드 세팅, 앱 용량 축소
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            // 디버그 버전, 공용 프로퍼티(BASE URL)
            buildConfigField(
                "String",
                "BASE_URL",
                gradleLocalProperties(rootDir).getProperty("debug.base.url"),
            )
        }
        release {
            // 릴리즈 버전, 빌드 세팅(앱 네임, 아이콘)
            manifestPlaceholders.putAll(
                mapOf(
                    "appName" to "@string/app_name",
                    "appIcon" to "@mipmap/ic_wss_logo",
                    "roundIcon" to "@mipmap/ic_wss_logo_round",
                ),
            )

            // 프로가드 세팅, 앱 용량 축소
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            // 릴리즈 버전, 공용 프로퍼티(BASE URL)
            buildConfigField(
                "String",
                "BASE_URL",
                gradleLocalProperties(rootDir).getProperty("release.base.url"),
            )
        }
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
}

dependencies {
    implementation(project(":core:resource"))

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
    implementation(libs.firebase.messaging)

    implementation(libs.amplitude)
}
