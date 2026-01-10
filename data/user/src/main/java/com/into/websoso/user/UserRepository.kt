package com.into.websoso.user

import com.into.websoso.core.network.datasource.user.UserApi
import com.into.websoso.user.datasource.UserLocalDataSource
import com.into.websoso.user.mapper.toData
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
            saveUserInfo(userInfo.userId)
            return userInfo
        }

        private suspend fun saveUserInfo(userId: Long) {
            userLocalDataSource.updateUserId(userId)
        }

        suspend fun fetchMyActivities(
            lastFeedId: Long,
            size: Int,
        ): UserFeedsEntity {
            val myUserId = fetchUserId()
            return fetchUserFeeds(myUserId, lastFeedId, size)
        }

        suspend fun fetchUserId(): Long = userLocalDataSource.getUserId()

        suspend fun fetchUserFeeds(
            userId: Long,
            lastFeedId: Long,
            size: Int,
        ): UserFeedsEntity = userApi.getUserFeeds(userId, lastFeedId, size).toData()
    }
