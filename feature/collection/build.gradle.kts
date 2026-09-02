import com.into.websoso.setNamespace

plugins {
    id("websoso.android.feature")
}

android {
    setNamespace("feature.collection")
}

dependencies {
    implementation(projects.data.library)

    implementation(libs.navigation.compose)
    implementation(libs.paging.compose)
}
