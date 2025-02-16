package com.into.websoso.data.mapper

import com.into.websoso.data.model.BlockedUsersEntity
import com.into.websoso.data.model.BlockedUsersEntity.BlockedUserEntity
import com.into.websoso.data.model.GenrePreferenceEntity
import com.into.websoso.data.model.MyProfileEntity
import com.into.websoso.data.model.NovelPreferenceEntity
import com.into.websoso.data.model.OtherUserProfileEntity
import com.into.websoso.data.model.TermsAgreementEntity
import com.into.websoso.data.model.UserFeedsEntity
import com.into.websoso.data.model.UserFeedsEntity.UserFeedEntity
import com.into.websoso.data.model.UserInfoDetailEntity
import com.into.websoso.data.model.UserInfoEntity
import com.into.websoso.data.model.UserNovelStatsEntity
import com.into.websoso.data.model.UserProfileStatusEntity
import com.into.websoso.data.model.UserStorageEntity
import com.into.websoso.data.model.UserStorageEntity.StorageNovelEntity
import com.into.websoso.data.remote.response.BlockedUsersResponseDto
import com.into.websoso.data.remote.response.GenrePreferenceResponseDto
import com.into.websoso.data.remote.response.MyProfileResponseDto
import com.into.websoso.data.remote.response.NovelPreferenceResponseDto
import com.into.websoso.data.remote.response.OtherUserProfileResponseDto
import com.into.websoso.data.remote.response.TermsAgreementResponseDto
import com.into.websoso.data.remote.response.UserFeedsResponseDto
import com.into.websoso.data.remote.response.UserFeedsResponseDto.UserFeedResponseDto
import com.into.websoso.data.remote.response.UserInfoDetailResponseDto
import com.into.websoso.data.remote.response.UserInfoResponseDto
import com.into.websoso.data.remote.response.UserNovelStatsResponseDto
import com.into.websoso.data.remote.response.UserProfileStatusResponseDto
import com.into.websoso.data.remote.response.UserStorageResponseDto
import com.into.websoso.data.remote.response.UserStorageResponseDto.StorageNovelDto
import com.into.websoso.ui.main.myPage.myActivity.model.Genres

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

fun TermsAgreementResponseDto.toData(): TermsAgreementEntity =
    TermsAgreementEntity(
        serviceAgreed = this.serviceAgreed,
        privacyAgreed = this.privacyAgreed,
        marketingAgreed = this.marketingAgreed,
    )
