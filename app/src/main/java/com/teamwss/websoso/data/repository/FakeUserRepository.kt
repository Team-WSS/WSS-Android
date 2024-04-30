package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.FeedEntity

class FakeUserRepository {
    val gender: String = "MALE"

    fun getUserInfo(): FeedEntity.UserEntity = FeedEntity.UserEntity(
        id = 0, nickname = "", profileImage = ""
    )
}
