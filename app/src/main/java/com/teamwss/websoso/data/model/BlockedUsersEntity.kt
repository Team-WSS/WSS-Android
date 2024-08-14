package com.teamwss.websoso.data.model

data class BlockedUsersEntity(
    val blockedUsers: List<BlockedUserEntity>
) {

    data class BlockedUserEntity(
        val blockId: Long,
        val userId: Long,
        val nickName: String,
        val avatarImage: String,
    )
}