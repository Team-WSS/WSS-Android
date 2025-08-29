import com.into.websoso.websosoDependencies

plugins {
    id("websoso.android.library")
    id("websoso.android.compose")
}

websosoDependencies {
    implementation(project(":core:resource"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
}
