import com.into.websoso.buildConfigs
import com.into.websoso.getLocalProperty
import com.into.websoso.manifestPlaceholders

plugins {
    id("websoso.android.application")
    id("websoso.android.hilt")
    id("websoso.android.compose")
    id("websoso.android.coroutines")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.parcelize)
}

android {
    namespace = "com.into.websoso"

    defaultConfig {
        applicationId = "com.into.websoso"
        versionCode = libs.versions.versionCode
            .get()
            .toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigs(rootDir) {
            string(name = "S3_BASE_URL", key = "s3.url")
            string(name = "KAKAO_APP_KEY", key = "kakao.app.key")
            string(name = "AMPLITUDE_KEY", key = "amplitude.key")
        }

        manifestPlaceholders {
            "kakaoAppKey" to getLocalProperty(rootDir, "kakao.app.key")
        }
    }

    buildTypes {

        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"

            manifestPlaceholders {
                "appName" to "@string/app_name_debug"
                "appIcon" to "@mipmap/ic_wss_logo_debug"
                "roundIcon" to "@mipmap/ic_wss_logo_debug_round"
            }
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            manifestPlaceholders {
                "appName" to "@string/app_name"
                "appIcon" to "@mipmap/ic_wss_logo"
                "roundIcon" to "@mipmap/ic_wss_logo_round"
            }
        }
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    // 프로젝트 의존성
    implementation(projects.core.resource)
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.auth)
    implementation(projects.core.authKakao)
    implementation(projects.core.network)
    implementation(projects.core.datastore)
    implementation(projects.core.database)

    implementation(projects.feature.signin)

    implementation(projects.data.account)
    implementation(projects.data.library)

    // AndroidX 및 Jetpack 기본 라이브러리
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.viewpager2)
    implementation(libs.fragment.ktx)
    implementation(libs.lifecycle.extensions)

    // 테스트 관련 라이브러리
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.espresso.core)

    // 보안 및 데이터 저장 관련 라이브러리
    implementation(libs.datastore.preferences)
    implementation(libs.security.crypto)

    // 네트워크 관련 라이브러리
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // 이미지 로딩 관련 라이브러리
    implementation(libs.coil)
    implementation(libs.coil.gif)
    implementation(libs.coil.svg)
    implementation(libs.coil.transformers)

    // UI 관련 유틸 라이브러리
    implementation(libs.dots.indicator) // ViewPager2 indicator
    implementation(libs.lottie) // Lottie 애니메이션
    implementation(libs.pull.to.refresh) // Pull 새로고침

    // Third-party SDK
    implementation(libs.kakao) // 카카오 로그인 API
    implementation(libs.amplitude) // Amplitude

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

    implementation("androidx.paging:paging-runtime:3.3.2")
}
