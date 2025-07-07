import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("domain.library")
}

dependencies {
    implementation(projects.data.library)
    implementation(libs.compose.paging)
}
