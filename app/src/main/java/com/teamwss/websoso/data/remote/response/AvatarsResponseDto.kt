package com.teamwss.websoso.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvatarsResponseDto(
    @SerialName("avatars")
    val avatars: List<AvatarResponseDto>
) {
    @Serializable
    data class AvatarResponseDto(
        @SerialName("avatarId")
        val avatarId: Int,
        @SerialName("avatarName")
        val avatarName: String,
        @SerialName("avatarLine")
        val avatarLine: String,
        @SerialName("avatarImage")
        val avatarImage: String,
        @SerialName("isRepresentative")
        val isRepresentative: Boolean
    )
}
