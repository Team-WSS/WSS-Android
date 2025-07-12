import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("data.library")
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.data.account)
    // 페이징3
    val paging_version = "3.3.6"

    implementation("androidx.paging:paging-runtime:$paging_version")

    // alternatively - without Android dependencies for tests
    implementation("androidx.paging:paging-common:$paging_version")
}
