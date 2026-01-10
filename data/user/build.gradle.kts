import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("data.user")
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.network)
}
