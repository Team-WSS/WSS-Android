import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("data.feed")
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.data.account)
    implementation(projects.core.network)
    implementation(projects.data.library)
    implementation(libs.paging.runtime)

    // TODO: 멀티 파트 빌드 오류를 해결하기 위한 임시 조치 -> 추후 네트워크 레이어로 마이그레이션
    implementation(libs.okhttp)
}
