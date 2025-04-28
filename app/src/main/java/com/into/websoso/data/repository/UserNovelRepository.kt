package com.into.websoso.data.repository

import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.NovelRatingEntity
import com.into.websoso.data.remote.api.UserNovelApi
import javax.inject.Inject

class UserNovelRepository
    @Inject
    constructor(
        private val userNovelApi: UserNovelApi,
    ) {
        suspend fun deleteUserNovel(novelId: Long) {
            userNovelApi.deleteUserNovel(novelId)
        }

        suspend fun fetchNovelRating(novelId: Long): NovelRatingEntity = userNovelApi.fetchNovelRating(novelId).toData()

        suspend fun saveNovelRating(
            novelRatingEntity: NovelRatingEntity,
            isAlreadyRated: Boolean,
        ) {
            when (isAlreadyRated) {
                true -> userNovelApi.putNovelRating(
                    novelRatingEntity.novelId
                        ?: throw IllegalArgumentException("novelId must not be null"),
                    novelRatingEntity.toData(),
                )

                false -> userNovelApi.postNovelRating(novelRatingEntity.toData())
            }
        }
    }
