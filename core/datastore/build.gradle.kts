import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("core.datastore")
}

dependencies {
    implementation(libs.datastore.preferences)
}
