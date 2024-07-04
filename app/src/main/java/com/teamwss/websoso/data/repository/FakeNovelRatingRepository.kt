package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.data.remote.response.NovelRatingKeywordResponseDto
import com.teamwss.websoso.data.remote.response.NovelRatingResponseDto
import javax.inject.Inject

class FakeNovelRatingRepository @Inject constructor() {

    suspend fun fetchNovelRating(userNovelId: Long): NovelRatingEntity {
        return dummyNovelRating.toData()
    }

    private val dummyNovelRating = NovelRatingResponseDto(
        novelTitle = "당신의 이해를 돕기 위하여",
        status = "WATCHED",
        startDate = "2021-05-11",
        endDate = "2023-06-14",
        userNovelRating = 4.5f,
        attractivePoints = listOf("vibe", "character"),
        keywords = listOf(
            NovelRatingKeywordResponseDto(1, "이세계"),
            NovelRatingKeywordResponseDto(4, "서양풍/중세시대"),
        )
    )
}
