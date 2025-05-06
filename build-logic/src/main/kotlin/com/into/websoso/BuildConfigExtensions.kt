package com.into.websoso

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.BuildType
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.File

fun ApplicationDefaultConfig.buildConfigs(
    rootDir: File,
    block: BuildConfigScope.() -> Unit,
) {
    val properties = gradleLocalProperties(rootDir)
    BuildConfigScope { name, key ->
        val value = properties.getProperty(key)
        buildConfigField("String", name, value)
    }.apply(block)
}

fun BuildType.buildConfigs(
    rootDir: File,
    block: BuildConfigScope.() -> Unit,
) {
    val properties = gradleLocalProperties(rootDir)
    BuildConfigScope { name, key ->
        val value = properties.getProperty(key)
        buildConfigField("String", name, value)
    }.apply(block)
}

fun getLocalProperty(
    rootDir: File,
    key: String,
): String = gradleLocalProperties(rootDir).getProperty(key).trim('"')

fun interface BuildConfigScope {
    fun string(
        name: String,
        key: String,
    )
}
