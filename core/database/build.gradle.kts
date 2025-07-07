import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("core.database")
}

dependencies {
    // 데이터베이스 관련 라이브러리
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)

    // 페이징3
    val paging_version = "3.3.6"
    implementation("androidx.room:room-paging:2.5.1")  // Room 최신 버전에 맞게 버전 확인


    implementation("androidx.paging:paging-runtime:$paging_version")

    // alternatively - without Android dependencies for tests
    implementation("androidx.paging:paging-common:$paging_version")
}
