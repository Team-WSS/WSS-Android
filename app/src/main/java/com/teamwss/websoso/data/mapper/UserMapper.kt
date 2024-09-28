package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.BlockedUsersEntity
import com.teamwss.websoso.data.model.BlockedUsersEntity.BlockedUserEntity
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.data.model.MyProfileEntity
import com.teamwss.websoso.data.model.NovelPreferenceEntity
import com.teamwss.websoso.data.model.OtherUserProfileEntity
import com.teamwss.websoso.data.model.UserFeedsEntity
import com.teamwss.websoso.data.model.UserFeedsEntity.UserFeedEntity
import com.teamwss.websoso.data.model.UserInfoDetailEntity
import com.teamwss.websoso.data.model.UserInfoEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.data.model.UserProfileStatusEntity
import com.teamwss.websoso.data.model.UserStorageEntity
import com.teamwss.websoso.data.model.UserStorageEntity.StorageNovelEntity
import com.teamwss.websoso.data.remote.response.BlockedUsersResponseDto
import com.teamwss.websoso.data.remote.response.GenrePreferenceResponseDto
import com.teamwss.websoso.data.remote.response.MyProfileResponseDto
import com.teamwss.websoso.data.remote.response.NovelPreferenceResponseDto
import com.teamwss.websoso.data.remote.response.OtherUserProfileResponseDto
import com.teamwss.websoso.data.remote.response.UserFeedsResponseDto
import com.teamwss.websoso.data.remote.response.UserFeedsResponseDto.UserFeedResponseDto
import com.teamwss.websoso.data.remote.response.UserInfoDetailResponseDto
import com.teamwss.websoso.data.remote.response.UserInfoResponseDto
import com.teamwss.websoso.data.remote.response.UserNovelStatsResponseDto
import com.teamwss.websoso.data.remote.response.UserProfileStatusResponseDto
import com.teamwss.websoso.data.remote.response.UserStorageResponseDto
import com.teamwss.websoso.data.remote.response.UserStorageResponseDto.StorageNovelDto
import com.teamwss.websoso.ui.main.myPage.myActivity.model.Genres

fun UserInfoResponseDto.toData(): UserInfoEntity {
    return UserInfoEntity(
        userId = this.userId,
        nickname = this.nickname,
        gender = this.gender,
    )
}

fun UserInfoDetailResponseDto.toData(): UserInfoDetailEntity {
    return UserInfoDetailEntity(
        email = email,
        gender = gender,
        birthYear = birth,
    )
}

fun BlockedUsersResponseDto.toData(): BlockedUsersEntity {
    return BlockedUsersEntity(blockedUsers = blocks.map { blockedUser ->
        BlockedUserEntity(
            blockId = blockedUser.blockId,
            userId = blockedUser.userId,
            nickName = blockedUser.nickName,
            avatarImage = blockedUser.avatarImage,
        )
    })
}

fun UserNovelStatsResponseDto.toData(): UserNovelStatsEntity {
    return UserNovelStatsEntity(
        interestNovelCount = interestNovelCount,
        watchingNovelCount = watchingNovelCount,
        watchedNovelCount = watchedNovelCount,
        quitNovelCount = quitNovelCount,
    )
}

fun UserProfileStatusResponseDto.toData(): UserProfileStatusEntity {
    return UserProfileStatusEntity(
        isProfilePublic = isProfilePublic,
    )
}

fun MyProfileResponseDto.toData(): MyProfileEntity {
    return MyProfileEntity(
        nickname = this.nickname,
        intro = this.intro,
        avatarImage = this.avatarImage,
        genrePreferences = this.genrePreferences,
    )
}

fun GenrePreferenceResponseDto.GenrePreferenceDto.toData(): GenrePreferenceEntity {
    val koreanGenreName = Genres.from(this.genreName)?.korean ?: this.genreName
    return GenrePreferenceEntity(
        genreName = koreanGenreName,
        genreImage = this.genreImage,
        genreCount = this.genreCount,
    )
}

fun NovelPreferenceResponseDto.toData(): NovelPreferenceEntity {
    return NovelPreferenceEntity(
        attractivePoints = this.attractivePoints,
        keywords = this.keywords.map { it.toData() },
    )
}

fun NovelPreferenceResponseDto.AttractivePointKeywordDto.toData(): NovelPreferenceEntity.KeywordEntity {
    return NovelPreferenceEntity.KeywordEntity(
        keywordName = this.keywordName,
        keywordCount = this.keywordCount,
    )
}

fun OtherUserProfileResponseDto.toData(): OtherUserProfileEntity {
    return OtherUserProfileEntity(
        nickname = this.nickname,
        intro = this.intro,
        avatarImage = this.avatarImage,
        isProfilePublic = this.isProfilePublic,
        genrePreferences = this.genrePreferences,
    )
}

fun UserStorageResponseDto.toData(): UserStorageEntity {
    return UserStorageEntity(
        isLoadable = this.isLoadable,
        userNovelRating = this.userNovelRating,
        userNovelCount = this.userNovelCount,
        userNovels = this.userNovels.map { it.toData() },
    )
}

fun StorageNovelDto.toData(): StorageNovelEntity {
    return StorageNovelEntity(
        author = this.author,
        userNovelId = this.userNovelId,
        novelId = this.novelId,
        novelRating = this.novelRating,
        novelImage = this.novelImage,
        title = this.title,
    )
}

fun UserFeedsResponseDto.toData(): UserFeedsEntity {
    return UserFeedsEntity(
        isLoadable = this.isLoadable,
        feeds = this.feeds.map { it.toData() },
    )
}

fun UserFeedResponseDto.toData(): UserFeedEntity {
    return UserFeedEntity(
        feedId = this.feedId,
        isSpoiler = this.isSpoiler,
        feedContent = this.feedContent,
        createdDate = this.createdDate,
        isModified = this.isModified,
        isLiked = this.isLiked,
        likeCount = this.likeCount,
        commentCount = this.commentCount,
        novelId = this.novelId,
        title = this.title,
        novelRatingCount = this.novelRatingCount,
        novelRating = this.novelRating,
        relevantCategories = this.relevantCategories,
    )
}