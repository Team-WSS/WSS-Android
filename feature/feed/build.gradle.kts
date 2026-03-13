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
    implementation(projects.core.resource)
    implementation(projects.data.feed)
    implementation(projects.domain.feed)
    implementation(libs.paging.compose)
    implementation(libs.androidx.foundation)
}
