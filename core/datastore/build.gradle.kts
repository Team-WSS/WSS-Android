import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("core.datastore")
}

dependencies {
    // 데이터 레이어 의존성
    implementation(projects.data.account)

    // Datastore 라이브러리
    implementation(libs.datastore.preferences)
}
