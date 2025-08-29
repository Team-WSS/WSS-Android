package com.into.websoso

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

fun Project.setNamespace(name: String) {
    androidExtension.apply {
        namespace = "com.into.websoso.$name"
    }
}

internal val Project.applicationExtension: CommonExtension<*, *, *, *, *>
    get() = extensions.getByType<ApplicationExtension>()

internal val Project.libraryExtension: CommonExtension<*, *, *, *, *>
    get() = extensions.getByType<LibraryExtension>()

internal val Project.androidExtension: CommonExtension<*, *, *, *, *>
    get() = runCatching { libraryExtension }
        .recoverCatching { applicationExtension }
        .onFailure { println("Could not find Library or Application extension from this project") }
        .getOrThrow()
