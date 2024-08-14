package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.UserEmailEntity
import com.teamwss.websoso.data.remote.api.UserApi
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi,
) {

    suspend fun fetchUserEmail(): UserEmailEntity {
        return userApi.getUserEmail().toData()
    }
}