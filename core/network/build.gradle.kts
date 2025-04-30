import com.into.websoso.buildConfigs
import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
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
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
}
