import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularNovelsResponseDto(
    @SerialName("popularNovels")
    val popularNovelResponseDtos: List<PopularNovelResponseDto>,
) {
    @Serializable
    data class PopularNovelResponseDto(
        @SerialName("avatarImage")
        val avatarImage: String?,
        @SerialName("feedContent")
        val feedContent: String,
        @SerialName("nickname")
        val nickname: String?,
        @SerialName("novelId")
        val novelId: Long,
        @SerialName("novelImage")
        val novelImage: String,
        @SerialName("title")
        val title: String,
    )
}
