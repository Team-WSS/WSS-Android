import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    setNamespace("data.novel")
}

dependencies {
    implementation(projects.core.network)

    implementation(libs.paging.runtime)
    implementation(libs.retrofit)
    implementation(libs.serialization.json)
}
