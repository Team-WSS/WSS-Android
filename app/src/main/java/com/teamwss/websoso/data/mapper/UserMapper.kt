package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.BlockedUsersEntity
import com.teamwss.websoso.data.model.BlockedUsersEntity.BlockedUserEntity
import com.teamwss.websoso.data.model.UserEmailEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.data.remote.response.BlockedUsersResponseDto
import com.teamwss.websoso.data.remote.response.UserEmailResponseDto
import com.teamwss.websoso.data.remote.response.UserNovelStatsResponseDto

fun UserEmailResponseDto.toData(): UserEmailEntity {
    return UserEmailEntity(
        email = email,
    )
}

fun BlockedUsersResponseDto.toData(): BlockedUsersEntity {
    return BlockedUsersEntity(
        blockedUsers = blocks.map { blockedUser ->
            BlockedUserEntity(
                blockId = blockedUser.blockId,
                userId = blockedUser.userId,
                nickName = blockedUser.nickName,
                avatarImage = blockedUser.avatarImage,
            )
        }
    )
}

fun UserNovelStatsResponseDto.toData(): UserNovelStatsEntity {
    return UserNovelStatsEntity(
        interestNovelCount = interestNovelCount,
        watchingNovelCount = watchingNovelCount,
        watchedNovelCount = watchedNovelCount,
        quitNovelCount = quitNovelCount,
    )
}