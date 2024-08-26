package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.BlockedUsersEntity
import com.teamwss.websoso.data.model.BlockedUsersEntity.BlockedUserEntity
import com.teamwss.websoso.data.model.UserUpdateInfoEntity
import com.teamwss.websoso.data.model.UserInfoEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.data.model.UserProfileStatusEntity
import com.teamwss.websoso.data.remote.request.UserInfoRequestDto
import com.teamwss.websoso.data.remote.request.UserProfileStatusRequestDto
import com.teamwss.websoso.data.remote.response.BlockedUsersResponseDto
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

fun UserProfileStatusEntity.toRemote(): UserProfileStatusRequestDto {
    return UserProfileStatusRequestDto(
        isProfilePublic = isProfilePublic,
    )
}

fun UserUpdateInfoEntity.toRemote(): UserInfoRequestDto {
    return UserInfoRequestDto(
        gender = gender,
        birth = birthYear,
    )
}