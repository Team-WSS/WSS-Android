import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("data.library")
}

dependencies {
    implementation("androidx.paging:paging-runtime:3.3.2")
}
