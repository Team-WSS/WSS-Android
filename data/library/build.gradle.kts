import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("data.library")
}

dependencies {
    implementation(libs.compose.paging)
}
