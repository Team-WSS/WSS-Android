package com.into.websoso.feature.library.filter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.extensions.isCloseTo
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary50
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.feature.library.model.RatingLevelUiModel
import com.into.websoso.feature.library.model.RatingLevelUiModel.FOUR
import com.into.websoso.feature.library.model.RatingLevelUiModel.FOUR_POINT_EIGHT
import com.into.websoso.feature.library.model.RatingLevelUiModel.FOUR_POINT_FIVE
import com.into.websoso.feature.library.model.RatingLevelUiModel.THREE_POINT_FIVE

@Composable
internal fun LibraryFilterBottomSheetNovelRatingGrid(
    selectedRating: Float,
    onRatingClick: (rating: RatingLevelUiModel) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            NovelRatingItem(
                title = "3.5 이상",
                modifier = Modifier
                    .weight(weight = 1f)
                    .clickable {
                        onRatingClick(THREE_POINT_FIVE)
                    },
                isSelected = selectedRating.isCloseTo(THREE_POINT_FIVE.value),
            )
            NovelRatingItem(
                title = "4.0 이상",
                modifier = Modifier
                    .weight(weight = 1f)
                    .clickable {
                        onRatingClick(FOUR)
                    },
                isSelected = selectedRating.isCloseTo(FOUR.value),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            NovelRatingItem(
                title = "4.5 이상",
                modifier = Modifier
                    .weight(weight = 1f)
                    .clickable {
                        onRatingClick(FOUR_POINT_FIVE)
                    },
                isSelected = selectedRating.isCloseTo(FOUR_POINT_FIVE.value),
            )
            NovelRatingItem(
                title = "4.8 이상",
                modifier = Modifier
                    .weight(weight = 1f)
                    .clickable {
                        onRatingClick(FOUR_POINT_EIGHT)
                    },
                isSelected = selectedRating.isCloseTo(FOUR_POINT_EIGHT.value),
            )
        }
    }
}

@Composable
private fun NovelRatingItem(
    title: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    val backgroundColor = if (isSelected) Primary50 else Gray50

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(size = 8.dp),
            ).then(
                if (isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = Primary100,
                        shape = RoundedCornerShape(size = 8.dp),
                    )
                } else {
                    Modifier
                },
            ).padding(vertical = 14.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = WebsosoTheme.typography.body2,
            color = if (isSelected) Primary100 else Gray300,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetNovelRatingGridPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetNovelRatingGrid(
            selectedRating = 0f,
            onRatingClick = {},
        )
    }
}
