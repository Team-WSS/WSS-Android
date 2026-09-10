import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    setNamespace("data.collection")
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.data.account)
    implementation(projects.domain.collection)

    implementation(libs.paging.runtime)
    implementation(libs.retrofit)
    implementation(libs.serialization.json)
}
