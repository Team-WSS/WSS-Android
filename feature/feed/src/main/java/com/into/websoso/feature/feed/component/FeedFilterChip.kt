package com.into.websoso.feature.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray80
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White

@Composable
internal fun FeedFilterChip(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    leftIcon: (@Composable () -> Unit)? = null,
    rightIcon: (@Composable () -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(
                if (isSelected) {
                    Modifier.background(
                        color = Black,
                        shape = RoundedCornerShape(size = 18.dp),
                    )
                } else {
                    Modifier
                        .border(
                            width = 1.dp,
                            color = Gray80,
                            shape = RoundedCornerShape(size = 18.dp),
                        )
                        .background(
                            color = White,
                            shape = RoundedCornerShape(size = 18.dp),
                        )
                },
            )
            .padding(horizontal = 13.dp),
    ) {
        leftIcon?.let {
            leftIcon()
        }

        Text(
            text = label,
            style = WebsosoTheme.typography.body4,
            color = if (isSelected) White else Gray300,
            modifier = Modifier.padding(vertical = 7.dp),
        )

        rightIcon?.let {
            rightIcon()
        }
    }
}

@Preview
@Composable
private fun FeedFilterChipSelectedPreview() {
    WebsosoTheme {
        FeedFilterChip(
            label = "label",
            isSelected = true,
        )
    }
}

@Preview
@Composable
private fun FeedFilterChipNonSelectedPreview() {
    WebsosoTheme {
        FeedFilterChip(
            label = "label",
            isSelected = false,
        )
    }
}
