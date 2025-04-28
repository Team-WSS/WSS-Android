package com.into.websoso.data.repository

import com.into.websoso.BuildConfig
import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.MinimumVersionEntity
import com.into.websoso.data.remote.api.VersionApi
import javax.inject.Inject

class VersionRepository
    @Inject
    constructor(
        private val versionApi: VersionApi,
    ) {
        suspend fun isUpdateRequired(): Boolean {
            val currentVersion = BuildConfig.VERSION_NAME
            val minVersion = fetchMinimumVersion().minimumVersion

            return compareVersions(parseVersionName(currentVersion), parseVersionName(minVersion))
        }

        private suspend fun fetchMinimumVersion(): MinimumVersionEntity = versionApi.getMinimumVersion(os = OS).toData()

        private fun parseVersionName(version: String): List<Int> = version.split(".").map { it.toIntOrNull() ?: 0 }

        private fun compareVersions(
            currentVersionParts: List<Int>,
            minVersionParts: List<Int>,
        ): Boolean {
            for (i in 0 until maxOf(currentVersionParts.size, minVersionParts.size)) {
                val current = currentVersionParts.getOrNull(i) ?: 0
                val min = minVersionParts.getOrNull(i) ?: 0
                if (current < min) return true
                if (current > min) return false
            }
            return false
        }

        companion object {
            private const val OS = "android"
        }
    }
