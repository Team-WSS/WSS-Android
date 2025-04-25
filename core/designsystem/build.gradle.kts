plugins {
    alias(libs.plugins.android.library)
    id("websoso.android.compose")
    id("websoso.android.kotlin")
}

android {
    namespace = "com.into.websoso.designsystem"
}

dependencies {
    // 프로젝트 의존성
    implementation(projects.core.resource)

    // 이미지 처리 라이브러리
    implementation(libs.coil)
}
