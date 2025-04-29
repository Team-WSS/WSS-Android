import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("core.auth_kakao")
}

dependencies {
    implementation(projects.core.auth)

    implementation(libs.kakao)
}
