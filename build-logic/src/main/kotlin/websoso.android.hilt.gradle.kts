import gradle.kotlin.dsl.accessors._80a2ae57395e1362b61311ead0eb480f.implementation

plugins {
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

dependencies {
    // 기본 Hilt 라이브러리
    implementation("com.google.dagger:hilt-android:2.51.1")

    // Hilt 컴파일러 (Dagger 코드 생성)
    add("kapt","com.google.dagger:hilt-compiler:2.51.1")
}
