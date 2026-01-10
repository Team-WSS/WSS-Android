import com.into.websoso.androidExtension
import com.into.websoso.websosoDependencies

plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

androidExtension.apply {
    buildFeatures.compose = true
}

websosoDependencies {
    implementation(platform("compose.bom"))
    androidTestImplementation(platform("compose.bom"))

    implementation("compose")
    implementation("kotlinx-collections-immutable")
}
