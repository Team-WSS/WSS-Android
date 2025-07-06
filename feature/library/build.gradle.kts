import com.into.websoso.setNamespace

plugins {
    id("websoso.android.feature")
}

android {
    setNamespace("feature.library")
}

dependencies {
    implementation(projects.data.library)
    implementation(projects.core.resource)
    implementation(projects.core.designsystem)
    implementation(projects.domain.library)
    implementation(projects.data.library)

    implementation("androidx.paging:paging-runtime:3.3.2")
    implementation(libs.androidx.paging.compose.android)
}
