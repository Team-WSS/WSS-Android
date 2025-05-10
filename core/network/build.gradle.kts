import com.into.websoso.buildConfigs
import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    setNamespace("core.network")
    buildTypes {
        debug {
            buildConfigs(rootDir) {
                string(name = "BASE_URL", key = "debug.base.url")
            }
        }

        release {
            buildConfigs(rootDir) {
                string(name = "BASE_URL", key = "release.base.url")
            }
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // 데이터 레이어 의존성
    implementation(projects.data.account)

    // 프로젝트 의존성
    implementation(projects.core.auth)
    implementation(projects.core.common)

    // 네트워크 관련 라이브러리
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
}
