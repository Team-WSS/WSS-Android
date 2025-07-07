import com.into.websoso.setNamespace

plugins {
    id("websoso.android.feature")
}

android {
    setNamespace("feature.library")
}

dependencies {
    implementation(projects.core.resource)
    implementation(projects.core.designsystem)
    implementation(projects.domain.library)
    implementation(projects.data.library)

    implementation(libs.compose.paging)
}
