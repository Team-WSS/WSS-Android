import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("data.account")
}

dependencies {
    implementation(projects.core.auth)
}
