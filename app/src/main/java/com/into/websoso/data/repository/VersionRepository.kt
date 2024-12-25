package com.into.websoso.data.repository

import com.into.websoso.BuildConfig
import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.MinimumVersionEntity
import com.into.websoso.data.remote.api.VersionApi
import javax.inject.Inject

class VersionRepository @Inject constructor(
    private val versionApi: VersionApi,
) {
    suspend fun isUpdateRequired(): Boolean {
        val currentVersionCode = BuildConfig.VERSION_CODE
        val minVersionCode = parseVersionCode(fetchMinimumVersion().minimumVersion)
        return currentVersionCode < minVersionCode
    }

    private suspend fun fetchMinimumVersion(): MinimumVersionEntity {
        return versionApi.getMinimumVersion(os = OS).toData()
    }

    private fun parseVersionCode(version: String): Int {
        val parts = version.split(".").map { it.toIntOrNull() ?: 0 }
        val major = parts.getOrNull(0) ?: 0
        val minor = parts.getOrNull(1) ?: 0
        val patch = parts.getOrNull(2) ?: 0
        return major * 1000000 + minor * 1000 + patch
    }

    companion object {
        private const val OS = "android"
    }
}