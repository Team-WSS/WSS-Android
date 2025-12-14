package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvatarsResponseDto(
    @SerialName("avatarProfiles")
    val avatars: List<AvatarResponseDto>,
) {
    @Serializable
    data class AvatarResponseDto(
        @SerialName("avatarProfileId")
        val avatarId: Long,
        @SerialName("avatarProfileName")
        val avatarName: String,
        @SerialName("avatarProfileLine")
        val avatarLine: String,
        @SerialName("avatarProfileImage")
        val avatarProfileImage: String,
        @SerialName("avatarProfileCharacterImage")
        val avatarImage: String,
        @SerialName("isRepresentative")
        val isRepresentative: Boolean,
    )
}
