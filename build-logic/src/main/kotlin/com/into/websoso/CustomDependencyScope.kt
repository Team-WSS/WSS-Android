package com.into.websoso

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType
import kotlin.jvm.optionals.getOrNull

internal class CustomDependencyScope(
    private val project: Project,
    private val dependencies: DependencyHandlerScope,
) {
    private val libs = project
        .extensions
        .getByType<VersionCatalogsExtension>()
        .named("libs")

    private fun safeFindLibrary(alias: String): Provider<MinimalExternalModuleDependency> {
        return libs.findLibrary(alias).getOrNull()
            ?: error("'$alias' not found in libs.versions.toml.")
    }

    private fun safeFindBundle(alias: String): Provider<ExternalModuleDependencyBundle> {
        return libs.findBundle(alias).getOrNull()
            ?: error("'$alias' bundle not found in libs.versions.toml.")
    }

    private fun addDependency(configuration: String, dependency: Any) {
        dependencies.add(configuration, dependency)
    }

    private fun handleStringDependency(alias: String, configuration: String) {
        val dependency = if (isBundle(alias)) safeFindBundle(alias) else safeFindLibrary(alias)
        addDependency(configuration, dependency)
    }

    private fun isBundle(alias: String): Boolean {
        return libs.findBundle(alias).getOrNull() != null
    }

    fun platform(alias: String): Provider<MinimalExternalModuleDependency> {
        val dependency = safeFindLibrary(alias)
        return project.dependencies.platform(dependency)
    }

    fun kapt(alias: Any) {
        if (alias is String) handleStringDependency(alias, "kapt")
        else addDependency("kapt", alias)
    }

    fun implementation(alias: Any) {
        if (alias is String) handleStringDependency(alias, "implementation")
        else addDependency("implementation", alias)
    }

    fun androidTestImplementation(alias: Any) {
        if (alias is String) handleStringDependency(alias, "androidTestImplementation")
        else addDependency("androidTestImplementation", alias)
    }

    fun debugImplementation(alias: Any) {
        if (alias is String) handleStringDependency(alias, "debugImplementation")
        else addDependency("debugImplementation", alias)
    }

    fun testImplementation(alias: Any) {
        if (alias is String) handleStringDependency(alias, "testImplementation")
        else addDependency("testImplementation", alias)
    }
}
