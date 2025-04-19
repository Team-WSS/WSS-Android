import com.into.websoso.websosoDependencies

plugins {
    kotlin("kapt")
}

websosoDependencies {
    implementation("dagger")
    kapt("dagger.compiler")
}
