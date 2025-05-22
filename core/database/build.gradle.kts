import com.into.websoso.buildConfigs
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

    // 데이터베이스 관련 라이브러리
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
}
