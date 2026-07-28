import com.into.websoso.setNamespace

plugins {
    id("websoso.android.feature")
}

android {
    setNamespace("feature.collection")
}

dependencies {
    implementation(libs.navigation.compose)
}
