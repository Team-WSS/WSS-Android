import com.into.websoso.websosoDependencies

plugins {
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

websosoDependencies {
    implementation("hilt.android")
    kapt("hilt.compiler")
}
