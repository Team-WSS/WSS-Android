package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.mapper.toRemote
import com.teamwss.websoso.data.model.BlockedUsersEntity
import com.teamwss.websoso.data.model.ProfileEntity
import com.teamwss.websoso.data.model.UserUpdateInfoEntity
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

    suspend fun saveUserInfo(gender: String, birthYear: Int) {
        val userInfo = UserUpdateInfoEntity(gender, birthYear)
        userApi.putUserInfo(userInfo.toRemote())
    }

    suspend fun fetchNicknameValidity(nickname: String): Boolean {
        return userApi.getNicknameValidity(nickname).isValid
    }

    suspend fun saveUserProfile(avatarId: Int?, nickname: String?, intro: String?, genrePreferences: List<String>) {
        userApi.patchProfile(ProfileEntity(avatarId, nickname, intro, genrePreferences).toRemote())
    }
}
