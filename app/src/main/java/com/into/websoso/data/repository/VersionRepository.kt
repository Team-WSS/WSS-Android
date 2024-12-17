package com.into.websoso.data.repository

import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.MinimumVersionEntity
import com.into.websoso.data.remote.api.VersionApi
import javax.inject.Inject

class VersionRepository @Inject constructor(
    private val versionApi: VersionApi,
) {
    suspend fun fetchMinimumVersion(): MinimumVersionEntity {
        return versionApi.getMinimumVersion(os = "android").toData()
    }
}