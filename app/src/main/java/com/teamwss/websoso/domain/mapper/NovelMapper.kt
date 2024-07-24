package com.teamwss.websoso.domain.mapper

import com.teamwss.websoso.data.model.NovelDetailEntity
import com.teamwss.websoso.domain.model.NovelDetail

fun NovelDetailEntity.toDomain(
    novelGenres: List<String>,
    isNovelCompletedText: String,
): NovelDetail {
    return NovelDetail(
        userNovel = NovelDetail.UserNovel(
            userNovelId = this.userNovelId,
            readStatus = this.readStatus,
            startDate = this.startDate,
            endDate = this.endDate,
            isUserNovelInterest = this.isUserNovelInterest,
            userNovelRating = this.userNovelRating,
        ),
        novel = NovelDetail.Novel(
            novelTitle = this.novelTitle,
            novelImage = this.novelImage,
            novelGenres = novelGenres,
            novelGenreImage = this.novelGenreImage,
            isNovelCompletedText = isNovelCompletedText,
            author = this.author,
        ),
        userRating = NovelDetail.UserRating(
            interestCount = this.interestCount,
            novelRating = this.novelRating,
            novelRatingCount = this.novelRatingCount,
            feedCount = this.feedCount,
        )
    )
}
