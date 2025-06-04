import com.into.websoso.setNamespace

plugins {
    id("websoso.android.feature")
}

android {
    setNamespace("feature.library")
}

dependencies {
    implementation(projects.domain.library)

    implementation("androidx.paging:paging-runtime:3.3.2")
}
