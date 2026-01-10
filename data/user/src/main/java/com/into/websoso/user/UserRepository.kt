package com.into.websoso.user

import com.into.websoso.core.network.datasource.user.UserApi
import com.into.websoso.user.datasource.UserLocalDataSource
import com.into.websoso.user.mapper.toData
import com.into.websoso.user.model.MyProfileEntity
import com.into.websoso.user.model.UserFeedsEntity
import com.into.websoso.user.model.UserInfoEntity
import jakarta.inject.Inject

class UserRepository
    @Inject
    constructor(
        private val userApi: UserApi,
        private val userLocalDataSource: UserLocalDataSource,
    ) {
        suspend fun fetchUserInfo(): UserInfoEntity {
            val userInfo = userApi.getUserInfo().toData()
            saveUserInfo(userInfo.userId, userInfo.nickname, userInfo.gender)
            return userInfo
        }

        private suspend fun saveUserInfo(
            userId: Long,
            nickname: String,
            gender: String,
        ) {
            userLocalDataSource.updateUserInfo(userId, nickname, gender)
        }

        suspend fun fetchMyActivities(
            lastFeedId: Long,
            size: Int,
            genres: Array<String>?,
            isVisible: Boolean?,
            isUnVisible: Boolean?,
            sortCriteria: String?,
        ): UserFeedsEntity {
            val myUserId = fetchUserInfo().userId
            return fetchUserFeeds(
                myUserId,
                lastFeedId,
                size,
                genres,
                isVisible,
                isUnVisible,
                sortCriteria,
            )
        }

        suspend fun fetchUserFeeds(
            userId: Long,
            lastFeedId: Long,
            size: Int,
            genres: Array<String>? = null,
            isVisible: Boolean? = null,
            isUnVisible: Boolean? = null,
            sortCriteria: String? = null,
        ): UserFeedsEntity =
            userApi
                .getUserFeeds(
                    userId,
                    lastFeedId,
                    size,
                    genres,
                    isVisible,
                    isUnVisible,
                    sortCriteria,
                ).toData()

        suspend fun fetchMyProfile(): MyProfileEntity = userApi.getMyProfile().toData()
    }
