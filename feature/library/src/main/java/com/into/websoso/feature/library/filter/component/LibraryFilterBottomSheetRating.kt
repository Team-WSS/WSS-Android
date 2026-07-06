package com.into.websoso.feature.library.filter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.domain.library.model.RatingFilter
import java.util.Locale

@Composable
internal fun LibraryFilterBottomSheetRating(
    ratingFilter: RatingFilter,
    onRangeChange: (Float, Float) -> Unit,
    onRatinglessToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val enabled = !ratingFilter.isRatingless

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            RatingValueBox(value = ratingFilter.min, enabled = enabled)
            Box(modifier = Modifier.weight(1f)) {
                RatingRangeSlider(
                    min = ratingFilter.min,
                    max = ratingFilter.max,
                    valueRange = RatingFilter.RATING_MIN..RatingFilter.RATING_MAX,
                    stepSize = RatingFilter.RATING_STEP,
                    onValueChange = onRangeChange,
                    enabled = enabled,
                    showTicks = ratingFilter.min > RatingFilter.RATING_MIN ||
                        ratingFilter.max < RatingFilter.RATING_MAX,
                )
            }
            RatingValueBox(value = ratingFilter.max, enabled = enabled)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "별점 등록 안된 작품만 보기",
                style = WebsosoTheme.typography.title2,
                color = Gray300,
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = ratingFilter.isRatingless,
                onCheckedChange = { onRatinglessToggle() },
                modifier = Modifier.scale(0.85f),
                thumbContent = {},
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = Primary100,
                    checkedBorderColor = Primary100,
                    uncheckedThumbColor = White,
                    uncheckedTrackColor = Gray100,
                    uncheckedBorderColor = Gray100,
                ),
            )
        }
    }
}

@Composable
private fun RatingValueBox(
    value: Float,
    enabled: Boolean,
) {
    Box(
        modifier = Modifier
            .size(width = 50.dp, height = 38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Gray50),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = String.format(Locale.US, "%.1f", value),
            style = WebsosoTheme.typography.body2,
            color = if (enabled) Primary100 else Gray200,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetRatingPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetRating(
            ratingFilter = RatingFilter(min = 3.5f, max = 5.0f),
            onRangeChange = { _, _ -> },
            onRatinglessToggle = {},
        )
    }
}
