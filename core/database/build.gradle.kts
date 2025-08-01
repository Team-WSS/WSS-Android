import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("core.database")
}

dependencies {
    // 데이터 레이어 의존성
    implementation(projects.data.library)
    implementation(projects.core.model)

    // 페이징 관련 의존성
    implementation(libs.paging.runtime)

    // 데이터베이스 관련 의존성
    implementation(libs.room.paging)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
}
