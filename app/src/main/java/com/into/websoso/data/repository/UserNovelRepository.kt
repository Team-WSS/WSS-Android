package com.into.websoso.data.repository

import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.NovelRatingEntity
import com.into.websoso.data.remote.api.UserNovelApi
import javax.inject.Inject

class UserNovelRepository
    @Inject
    constructor(
        private val userNovelApi: UserNovelApi,
        private val libraryLocalDataSource: LibraryLocalDataSource,
    ) {
        suspend fun deleteUserNovel(novelId: Long) {
            libraryLocalDataSource.deleteNovel(novelId)
            userNovelApi.deleteUserNovel(novelId)
        }

        suspend fun fetchNovelRating(novelId: Long): NovelRatingEntity = userNovelApi.fetchNovelRating(novelId).toData()

        suspend fun saveNovelRating(
            feeds: List<String>,
            novelRatingEntity: NovelRatingEntity,
            isInterested: Boolean?,
            isAlreadyRated: Boolean,
        ) {
            val updatedNovel = NovelEntity(
                userNovelId = novelRatingEntity.userNovelId
                    ?: libraryLocalDataSource.selectAllNovelsCount().toLong(),
                novelId = novelRatingEntity.novelId!!,
                title = novelRatingEntity.novelTitle.orEmpty(),
                novelImage = novelRatingEntity.novelImage.orEmpty(),
                novelRating = novelRatingEntity.novelRating,
                readStatus = novelRatingEntity.readStatus.orEmpty(),
                isInterest = isInterested ?: false,
                userNovelRating = novelRatingEntity.userNovelRating,
                attractivePoints = novelRatingEntity.charmPoints,
                startDate = novelRatingEntity.startDate.orEmpty(),
                endDate = novelRatingEntity.endDate.orEmpty(),
                keywords = novelRatingEntity.userKeywords.map { it.keywordName },
                myFeeds = feeds,
            )

            libraryLocalDataSource.insertNovel(updatedNovel)

            if (isAlreadyRated) {
                userNovelApi.putNovelRating(novelRatingEntity.novelId, novelRatingEntity.toData())
            } else {
                userNovelApi.postNovelRating(novelRatingEntity.toData())
            }
        }
    }
