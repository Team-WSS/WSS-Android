plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.into.websoso.core.resource"
    compileSdk = 35

    defaultConfig {
        minSdk = 30
    }
}
