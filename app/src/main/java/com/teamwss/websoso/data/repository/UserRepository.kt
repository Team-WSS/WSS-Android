package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.BlockedUsersEntity
import com.teamwss.websoso.data.model.MyProfileEntity
import com.teamwss.websoso.data.model.UserInfoEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.data.model.UserProfileStatusEntity
import com.teamwss.websoso.data.remote.api.UserApi
import com.teamwss.websoso.data.remote.request.UserInfoRequestDto
import com.teamwss.websoso.data.remote.request.UserProfileStatusRequestDto
import com.teamwss.websoso.data.remote.response.MyProfileResponseDto
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

    suspend fun saveUserProfileStatus(isProfilePublic: Boolean) {
        userApi.patchProfileStatus(UserProfileStatusRequestDto(isProfilePublic))
    }

    suspend fun saveUserInfo(gender: String, birthYear: Int) {
        userApi.putUserInfo(UserInfoRequestDto(gender, birthYear))
    }

    suspend fun fetchMyProfile(): MyProfileEntity{
        return userApi.getMyProfile().toData()
    }
}
