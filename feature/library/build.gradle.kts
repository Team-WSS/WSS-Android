import com.into.websoso.setNamespace

plugins {
    id("websoso.android.feature")
}

android {
    setNamespace("feature.library")
}

dependencies {
    implementation(projects.data.library)
}
