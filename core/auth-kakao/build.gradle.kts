import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("core.auth_kakao")

    buildTypes {
        release {
            isMinifyEnabled = false

            consumerProguardFiles("consumer-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.core.auth)

    implementation(libs.kakao)
}
