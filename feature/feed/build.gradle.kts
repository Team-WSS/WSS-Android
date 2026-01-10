import com.into.websoso.setNamespace

plugins {
    id("websoso.android.feature")
}

android {
    setNamespace("feature.feed")
}

dependencies {
    implementation(projects.core.resource)
    implementation(projects.core.designsystem)

    implementation(libs.paging.compose)
}
