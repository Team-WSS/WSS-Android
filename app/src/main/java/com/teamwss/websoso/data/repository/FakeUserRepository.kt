package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.FeedEntity
import javax.inject.Inject

class FakeUserRepository @Inject constructor(){
    val gender: String = "MALE"

    fun getUserInfo(): FeedEntity.UserEntity = FeedEntity.UserEntity(
        id = 0, nickname = "", profileImage = ""
    )
}
