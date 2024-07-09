import com.teamwss.websoso.data.remote.response.NovelRatingKeywordCategoryResponseDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelRatingKeywordCategoriesResponseDto(
    @SerialName("categories")
    val categories: List<NovelRatingKeywordCategoryResponseDto>,
)
