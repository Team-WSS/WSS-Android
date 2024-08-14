package com.teamwss.websoso.ui.blockUsers.model

data class BlockUsersModel(
    val blockUsers: List<BlockUserModel>
) {

    data class BlockUserModel(
        val blockId: Long,
        val userId: Long,
        val nickName: String,
        val avatarImage: String,
    )
}
