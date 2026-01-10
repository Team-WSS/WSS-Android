package com.into.websoso

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.BuildType
import java.io.File
import java.util.Properties

/**
 * local.properties를 읽는 함수
 */
fun getLocalProperties(rootDir: File): Properties {
    val properties = Properties()
    val localPropertiesFile = File(rootDir, "local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }
    return properties
}

fun ApplicationDefaultConfig.buildConfigs(
    rootDir: File,
    block: BuildConfigScope.() -> Unit,
) {
    val properties = getLocalProperties(rootDir)
    BuildConfigScope { name, key ->
        val rawValue = properties.getProperty(key) ?: ""
        val cleanedValue = rawValue.removeSurrounding("\"")

        buildConfigField("String", name, "\"$cleanedValue\"")
    }.apply(block)
}

fun BuildType.buildConfigs(
    rootDir: File,
    block: BuildConfigScope.() -> Unit,
) {
    val properties = getLocalProperties(rootDir)
    BuildConfigScope { name, key ->
        val rawValue = properties.getProperty(key) ?: ""
        val cleanedValue = rawValue.removeSurrounding("\"")

        buildConfigField("String", name, "\"$cleanedValue\"")
    }.apply(block)
}

fun getLocalProperty(
    rootDir: File,
    key: String,
): String = getLocalProperties(rootDir).getProperty(key).trim('"')

fun interface BuildConfigScope {
    fun string(
        name: String,
        key: String,
    )
}
