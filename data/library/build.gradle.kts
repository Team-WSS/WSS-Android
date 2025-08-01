import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("data.library")
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.data.account)
    implementation(libs.paging.runtime)
}
