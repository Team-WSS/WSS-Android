package com.teamwss.websoso.ui.blockedUsers.model

data class BlockedUsersModel(
    val blockedUsers: List<BlockedUserModel>
) {

    data class BlockedUserModel(
        val blockId: Long,
        val userId: Long,
        val nickName: String,
        val avatarImage: String,
    )
}