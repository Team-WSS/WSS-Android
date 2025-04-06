package com.into.websoso

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import kotlin.jvm.optionals.getOrNull

internal fun Project.websosoDependencies(block: CustomDependencyScope.() -> Unit) {
    dependencies {
        CustomDependencyScope(this@websosoDependencies, this).apply(block)
    }
}

internal class CustomDependencyScope(
    project: Project,
    private val dependencies: DependencyHandlerScope,
) {
    private val libs = project
        .extensions
        .getByType<VersionCatalogsExtension>()
        .named("libs")

    private fun safeFindLibrary(alias: String) = libs
        .findLibrary(alias)
        .getOrNull()
        ?: error("'$alias' not found in libs.versions.toml.")

    fun implementation(dependencyNotation: String) {
        dependencies.add("implementation", safeFindLibrary(dependencyNotation))
    }

    fun kapt(dependencyNotation: String) {
        dependencies.add("kapt", safeFindLibrary(dependencyNotation))
    }
}
