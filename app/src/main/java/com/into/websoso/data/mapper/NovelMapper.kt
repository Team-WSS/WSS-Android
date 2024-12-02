package com.into.websoso.data.mapper

import com.into.websoso.data.model.ExploreResultEntity
import com.into.websoso.data.model.ExploreResultEntity.NovelEntity
import com.into.websoso.data.model.NovelDetailEntity
import com.into.websoso.data.model.NovelFeedsEntity
import com.into.websoso.data.model.NovelInfoEntity
import com.into.websoso.data.model.PopularNovelsEntity
import com.into.websoso.data.model.RecommendedNovelsByUserTasteEntity
import com.into.websoso.data.model.SosoPickEntity
import com.into.websoso.data.remote.response.ExploreResultResponseDto
import com.into.websoso.data.remote.response.NovelDetailResponseDto
import com.into.websoso.data.remote.response.NovelFeedResponseDto
import com.into.websoso.data.remote.response.NovelInfoResponseDto
import com.into.websoso.data.remote.response.PopularNovelsResponseDto
import com.into.websoso.data.remote.response.RecommendedNovelsByUserTasteResponseDto
import com.into.websoso.data.remote.response.SosoPicksResponseDto

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
            novelGenres = novelGenres,
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
        }
    )
}

fun PopularNovelsResponseDto.toData(): PopularNovelsEntity {
    return PopularNovelsEntity(
        popularNovels = popularNovels.map { novel ->
            PopularNovelsEntity.PopularNovelEntity(
                avatarImage = novel.avatarImage,
                feedContent = novel.feedContent,
                nickname = novel.nickname,
                novelId = novel.novelId,
                novelImage = novel.novelImage,
                title = novel.title,
            )
        }
    )
}

fun RecommendedNovelsByUserTasteResponseDto.toData(): RecommendedNovelsByUserTasteEntity {
    return RecommendedNovelsByUserTasteEntity(
        tasteNovels = tasteNovels.map { novel ->
            RecommendedNovelsByUserTasteEntity.RecommendedNovelByUserTasteEntity(
                novelId = novel.novelId,
                title = novel.title,
                author = novel.author,
                novelImage = novel.novelImage,
                interestCount = novel.interestCount,
                novelRating = novel.novelRating,
                novelRatingCount = novel.novelRatingCount,
            )
        }
    )
}

fun NovelFeedResponseDto.toData(): NovelFeedsEntity {
    return NovelFeedsEntity(
        isLoadable = isLoadable,
        feeds = feeds.map { feed ->
            feed.toData()
        },
    )
}
