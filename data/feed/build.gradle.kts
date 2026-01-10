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
}
