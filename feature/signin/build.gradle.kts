import com.into.websoso.setNamespace

plugins {
    id("websoso.android.feature")
}

android {
    setNamespace("feature.signin")
}

dependencies {
    implementation(projects.core.auth)

    implementation(projects.data.account)
}
