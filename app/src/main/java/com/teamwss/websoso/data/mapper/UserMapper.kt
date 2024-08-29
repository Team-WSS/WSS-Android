package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.BlockedUsersEntity
import com.teamwss.websoso.data.model.BlockedUsersEntity.BlockedUserEntity
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.data.model.MyProfileEntity
import com.teamwss.websoso.data.model.NovelPreferenceEntity
import com.teamwss.websoso.data.model.UserInfoEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.data.model.UserProfileStatusEntity
import com.teamwss.websoso.data.remote.response.AttractivePointKeywordDto
import com.teamwss.websoso.data.remote.response.BlockedUsersResponseDto
import com.teamwss.websoso.data.remote.response.GenrePreferenceDto
import com.teamwss.websoso.data.remote.response.MyProfileResponseDto
import com.teamwss.websoso.data.remote.response.NovelPreferenceResponseDto
import com.teamwss.websoso.data.remote.response.UserInfoResponseDto
import com.teamwss.websoso.data.remote.response.UserNovelStatsResponseDto
import com.teamwss.websoso.data.remote.response.UserProfileStatusResponseDto

fun UserInfoResponseDto.toData(): UserInfoEntity {
    return UserInfoEntity(
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
        genrePreferences = this.genrePreferences
    )
}

fun GenrePreferenceDto.toData(): GenrePreferenceEntity {
    return GenrePreferenceEntity(
        genreName = this.genreName,
        genreImage = this.genreImage,
        genreCount = this.genreCount
    )
}

fun NovelPreferenceResponseDto.toData(): NovelPreferenceEntity {
    return NovelPreferenceEntity(
        attractivePoints = this.attractivePoints,
        keywords = this.keywords.map { it.toData() }
    )
}

fun AttractivePointKeywordDto.toData(): NovelPreferenceEntity.KeywordEntity {
    return NovelPreferenceEntity.KeywordEntity(
        keywordName = this.keywordName,
        keywordCount = this.keywordCount
    )
}

