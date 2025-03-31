import com.into.websoso.libs

plugins {
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

dependencies {
    "implementation"(libs.findLibrary("hilt.android").get())
    "kapt"(libs.findLibrary("hilt.android.compiler").get())
    "kaptAndroidTest"(libs.findLibrary("hilt.android.compiler").get())
}
