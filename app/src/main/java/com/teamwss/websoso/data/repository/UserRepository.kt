package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.mapper.toRemote
import com.teamwss.websoso.data.model.BlockedUsersEntity
import com.teamwss.websoso.data.model.UserInfoEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.data.model.UserProfileStatusEntity
import com.teamwss.websoso.data.remote.api.UserApi
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi,
) {

    suspend fun fetchUserInfo(): UserInfoEntity {
        return userApi.getUserInfo().toData()
    }

    suspend fun fetchBlockedUsers(): BlockedUsersEntity {
        return userApi.getBlockedUser().toData()
    }

    suspend fun deleteBlockedUser(blockId: Long) {
        userApi.deleteBlockedUser(blockId)
    }

    suspend fun fetchUserNovelStats(): UserNovelStatsEntity {
        return userApi.getUserNovelStats().toData()
    }

    suspend fun fetchUserProfileStatus(): UserProfileStatusEntity {
        return userApi.getProfileStatus().toData()
    }

    suspend fun saveUserProfileStatus(userProfileStatusEntity: UserProfileStatusEntity) {
        userApi.patchProfileStatus(userProfileStatusEntity.toRemote())
    }
}