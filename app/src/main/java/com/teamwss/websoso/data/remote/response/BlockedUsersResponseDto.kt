package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockedUsersResponseDto(
    @SerialName("blocks")
    val blocks: List<BlockedUserResponseDto>
) {

    @Serializable
    data class BlockedUserResponseDto(
        @SerialName("blockId")
        val blockId: Long,
        @SerialName("userId")
        val userId: Long,
        @SerialName("nickName")
        val nickName: String,
        @SerialName("avatarImage")
        val avatarImage: String,
    )
}
