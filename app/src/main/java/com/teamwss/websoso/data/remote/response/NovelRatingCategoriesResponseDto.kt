import com.teamwss.websoso.data.remote.response.NovelRatingCategoryResponseDto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelRatingCategoriesResponseDto(
    @SerialName("categories")
    val categories: List<NovelRatingCategoryResponseDto>,
)
