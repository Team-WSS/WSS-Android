package com.into.websoso.user.datasource

import com.into.websoso.user.model.UserInfoEntity

interface UserLocalDataSource {
    suspend fun getUserInfo(): UserInfoEntity

    suspend fun updateUserInfo(
        userId: Long,
        nickname: String,
        gender: String,
    )
}
