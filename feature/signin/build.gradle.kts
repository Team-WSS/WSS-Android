import com.into.websoso.setNamespace

plugins {
    id("websoso.android.feature")
}

android {
    setNamespace("feature.signin")

    buildTypes {
        release {
            isMinifyEnabled = false

            consumerProguardFiles("consumer-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.core.auth)

    implementation(projects.data.account)
}
