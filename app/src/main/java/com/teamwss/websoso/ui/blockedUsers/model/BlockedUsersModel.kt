package com.teamwss.websoso.ui.blockedUsers.model

data class BlockedUsersModel(
    val blockUsers: List<BlockUserModel>
) {

    data class BlockUserModel(
        val blockId: Long,
        val userId: Long,
        val nickName: String,
        val avatarImage: String,
    )
}
