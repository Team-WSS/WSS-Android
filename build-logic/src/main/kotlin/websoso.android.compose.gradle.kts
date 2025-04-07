import com.into.websoso.androidExtension
import com.into.websoso.websosoDependencies

androidExtension.apply {
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = "1.5.2"
}

websosoDependencies {
    implementation(platform("compose.bom"))
    androidTestImplementation(platform("compose.bom"))

    implementation("compose")
}
