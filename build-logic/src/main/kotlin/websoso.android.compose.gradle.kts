import com.into.websoso.androidExtension
import com.into.websoso.websosoDependencies

androidExtension.apply {
    buildFeatures {
        compose = true
    }
}

websosoDependencies {
    implementation(platform("compose.bom"))
    androidTestImplementation(platform("compose.bom"))

    implementation("compose")
}
