package com.into.websoso.user.datasource

interface UserLocalDataSource {
    suspend fun getUserId(): Long

    suspend fun updateUserId(userId: Long)
}
