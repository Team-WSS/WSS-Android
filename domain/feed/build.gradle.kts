import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("domain.feed")
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.data.feed)
}
