package com.into.websoso.data.repository

import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.AvatarEntity
import com.into.websoso.data.remote.api.AvatarApi
import javax.inject.Inject

class AvatarRepository
    @Inject
    constructor(
        private val avatarApi: AvatarApi,
    ) {
        suspend fun fetchAvatars(): List<AvatarEntity> = avatarApi.getAvatars().avatars.map { it.toData() }
    }
