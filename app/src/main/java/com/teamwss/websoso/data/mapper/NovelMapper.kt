package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.ExploreResultEntity
import com.teamwss.websoso.data.model.ExploreResultEntity.NovelEntity
import com.teamwss.websoso.data.model.NovelDetailEntity
import com.teamwss.websoso.data.model.NovelInfoEntity
import com.teamwss.websoso.data.model.SosoPickEntity
import com.teamwss.websoso.data.remote.response.ExploreResultResponseDto
import com.teamwss.websoso.data.remote.response.NovelDetailResponseDto
import com.teamwss.websoso.data.remote.response.NovelInfoResponseDto
import com.teamwss.websoso.data.remote.response.SosoPicksResponseDto

fun NovelDetailResponseDto.toData(): NovelDetailEntity {
    return NovelDetailEntity(
        userNovel =
        NovelDetailEntity.UserNovelEntity(
            userNovelId = userNovelId,
            readStatus = readStatus,
            startDate = startDate,
            endDate = endDate,
            isUserNovelInterest = isUserNovelInterest,
            userNovelRating = userNovelRating,
        ),
        novel =
        NovelDetailEntity.NovelEntity(
            novelTitle = novelTitle,
            novelImage = novelImage,
            novelGenres = novelGenres.split(","),
            novelGenreImage = novelGenreImage,
            isNovelCompleted = isNovelCompleted,
            author = author,
        ),
        userRating =
        NovelDetailEntity.UserRatingEntity(
            interestCount = interestCount,
            novelRating = novelRating,
            novelRatingCount = novelRatingCount,
            feedCount = feedCount,
        ),
    )
}

fun NovelInfoResponseDto.toData(): NovelInfoEntity {
    return NovelInfoEntity(
        novelDescription = novelDescription,
        platforms = platforms.map {
            NovelInfoEntity.PlatformEntity(
                platformName = it.platformName,
                platformImage = it.platformImage,
                platformUrl = it.platformUrl,
            )
        },
        attractivePoints = attractivePoints,
        keywords = keywords.map {
            NovelInfoEntity.KeywordEntity(
                keywordName = it.keywordName,
                keywordCount = it.keywordCount,
            )
        },
        reviewCount = NovelInfoEntity.ReviewCountEntity(
            watchingCount = watchingCount,
            watchedCount = watchedCount,
            quitCount = quitCount,
        ),
    )
}

fun SosoPicksResponseDto.toData(): SosoPickEntity {
    return SosoPickEntity(
        novels = sosoPicks.map { sosoPick ->
            SosoPickEntity.NovelEntity(
                novelId = sosoPick.novelId,
                novelTitle = sosoPick.title,
                novelCover = sosoPick.novelImage
            )
        }
    )
}

fun ExploreResultResponseDto.toData(): ExploreResultEntity {
    return ExploreResultEntity(
        resultCount = resultCount,
        isLoadable = isLoadable,
        novels = novels.map { novel ->
            NovelEntity(
                id = novel.novelId,
                title = novel.novelTitle,
                author = novel.novelAuthor,
                image = novel.novelImage,
                interestedCount = novel.interestedCount,
                rating = novel.novelRating,
                ratingCount = novel.novelRatingCount,
            )
        },
    )
}