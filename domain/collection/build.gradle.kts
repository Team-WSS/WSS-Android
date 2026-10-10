import com.into.websoso.setNamespace

plugins {
    id("websoso.android.library")
}

android {
    setNamespace("domain.collection")
}

dependencies {
    implementation(libs.paging.runtime)

    testImplementation(libs.junit)
}
