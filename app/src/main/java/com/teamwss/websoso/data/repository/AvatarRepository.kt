package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.mapper.toRemote
import com.teamwss.websoso.data.model.AvatarEntity
import com.teamwss.websoso.data.model.BlockedUsersEntity
import com.teamwss.websoso.data.model.UserInfoEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.data.model.UserProfileStatusEntity
import com.teamwss.websoso.data.model.UserUpdateInfoEntity
import com.teamwss.websoso.data.remote.api.AvatarApi
import com.teamwss.websoso.data.remote.api.UserApi
import com.teamwss.websoso.data.remote.request.UserProfileRequestDto
import javax.inject.Inject

class AvatarRepository @Inject constructor(
    private val avatarApi: AvatarApi,
) {

    suspend fun fetchAvatars(): List<AvatarEntity> {
        return avatarApi.getAvatars().avatars.map { it.toData() }
    }
}
