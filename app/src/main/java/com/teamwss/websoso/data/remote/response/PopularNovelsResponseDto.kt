import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularNovelsResponseDto(
    @SerialName("popularNovels")
    val popularNovels: List<PopularNovel>,
) {
    @Serializable
    data class PopularNovel(
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
