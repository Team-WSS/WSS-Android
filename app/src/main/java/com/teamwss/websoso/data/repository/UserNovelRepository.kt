package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.data.remote.api.UserNovelApi
import javax.inject.Inject

class UserNovelRepository @Inject constructor(
    private val userNovelApi: UserNovelApi,
) {

    suspend fun deleteUserNovel(novelId: Long) {
        userNovelApi.deleteUserNovel(novelId)
    }

    suspend fun fetchNovelRating(novelId: Long): NovelRatingEntity {
        return userNovelApi.fetchUserNovel(novelId).toData()
    }
}
