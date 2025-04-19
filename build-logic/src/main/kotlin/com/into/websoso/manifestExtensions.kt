package com.into.websoso

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.BuildType

fun ApplicationDefaultConfig.manifestPlaceholders(
    block: ManifestScope.() -> Unit,
) {
    ManifestScope { value ->
        this@manifestPlaceholders.manifestPlaceholders[this] = value
    }.apply(block)
}

fun BuildType.manifestPlaceholders(
    block: ManifestScope.() -> Unit,
) {
    ManifestScope { value ->
        this@manifestPlaceholders.manifestPlaceholders[this] = value
    }.apply(block)
}

fun interface ManifestScope {
    infix fun String.to(value: String)
}
