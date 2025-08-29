import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    setNamespace("core.datastore")
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.data.account)
    implementation(projects.data.library)

    implementation(libs.datastore.preferences)
    implementation(libs.serialization.json)
}
