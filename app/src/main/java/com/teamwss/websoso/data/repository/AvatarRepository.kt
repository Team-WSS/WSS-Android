package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.AvatarEntity
import com.teamwss.websoso.data.remote.api.AvatarApi
import javax.inject.Inject

class AvatarRepository @Inject constructor(
    private val avatarApi: AvatarApi,
) {

    suspend fun fetchAvatars(): List<AvatarEntity> {
        return avatarApi.getAvatars().avatars.map { it.toData() }
    }
}
