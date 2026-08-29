package com.into.websoso.ui.novelDetail.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray70
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.string.novel_notification_completion_description
import com.into.websoso.core.resource.R.string.novel_notification_completion_title
import com.into.websoso.core.resource.R.string.novel_notification_hiatus_return_description
import com.into.websoso.core.resource.R.string.novel_notification_hiatus_return_title
import com.into.websoso.ui.novelDetail.model.NovelNotificationUiState

@Composable
fun NovelNotificationContent(
    uiState: NovelNotificationUiState,
    onCompletionToggleClick: () -> Unit,
    onHiatusReturnToggleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentAlpha = when (uiState.isError) {
        true -> DISABLED_ALPHA
        false -> DEFAULT_ALPHA
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = White,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            ).padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        NovelNotificationToggleItem(
            title = stringResource(novel_notification_completion_title),
            description = stringResource(novel_notification_completion_description),
            isChecked = uiState.isCompletionNotificationEnabled,
            isEditable = uiState.isEditable,
            onClick = onCompletionToggleClick,
            modifier = Modifier.alpha(contentAlpha),
        )
        NovelNotificationToggleItem(
            title = stringResource(novel_notification_hiatus_return_title),
            description = stringResource(novel_notification_hiatus_return_description),
            isChecked = uiState.isHiatusReturnNotificationEnabled,
            isEditable = uiState.isEditable,
            onClick = onHiatusReturnToggleClick,
            modifier = Modifier.alpha(contentAlpha),
        )
    }
}

@Composable
private fun NovelNotificationToggleItem(
    title: String,
    description: String,
    isChecked: Boolean,
    isEditable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEditable,
                onClick = onClick,
            ).padding(vertical = 8.dp),
        verticalAlignment = CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = WebsosoTheme.typography.body2,
                color = Black,
            )
            Text(
                text = description,
                style = WebsosoTheme.typography.body5,
                color = Gray200,
            )
        }
        NovelNotificationToggle(isChecked = isChecked)
    }
}

@Composable
private fun NovelNotificationToggle(
    isChecked: Boolean,
    modifier: Modifier = Modifier,
) {
    val thumbOffset by animateDpAsState(
        targetValue = when (isChecked) {
            true -> TRACK_WIDTH - THUMB_SIZE - THUMB_MARGIN
            false -> THUMB_MARGIN
        },
        label = "NovelNotificationToggleThumbOffset",
    )

    Box(
        modifier = modifier
            .size(width = TRACK_WIDTH, height = TRACK_HEIGHT)
            .clip(CircleShape)
            .background(
                color = when (isChecked) {
                    true -> Primary100
                    false -> Gray70
                },
            ),
    ) {
        Box(
            modifier = Modifier
                .align(CenterStart)
                .offset(x = thumbOffset)
                .size(THUMB_SIZE)
                .clip(CircleShape)
                .background(White),
        )
    }
}

private val TRACK_WIDTH = 48.dp
private val TRACK_HEIGHT = 24.dp
private val THUMB_SIZE = 20.dp
private val THUMB_MARGIN = 2.dp
private const val DEFAULT_ALPHA = 1f
private const val DISABLED_ALPHA = 0.4f

@Preview
@Composable
private fun NovelNotificationContentPreview() {
    WebsosoTheme {
        NovelNotificationContent(
            uiState = NovelNotificationUiState(isLoading = false),
            onCompletionToggleClick = {},
            onHiatusReturnToggleClick = {},
        )
    }
}

@Preview
@Composable
private fun NovelNotificationContentPartiallyEnabledPreview() {
    WebsosoTheme {
        NovelNotificationContent(
            uiState = NovelNotificationUiState(
                isLoading = false,
                isCompletionNotificationEnabled = true,
            ),
            onCompletionToggleClick = {},
            onHiatusReturnToggleClick = {},
        )
    }
}

@Preview
@Composable
private fun NovelNotificationContentEnabledPreview() {
    WebsosoTheme {
        NovelNotificationContent(
            uiState = NovelNotificationUiState(
                isLoading = false,
                isCompletionNotificationEnabled = true,
                isHiatusReturnNotificationEnabled = true,
            ),
            onCompletionToggleClick = {},
            onHiatusReturnToggleClick = {},
        )
    }
}

@Preview
@Composable
private fun NovelNotificationContentErrorPreview() {
    WebsosoTheme {
        NovelNotificationContent(
            uiState = NovelNotificationUiState(
                isLoading = false,
                isError = true,
            ),
            onCompletionToggleClick = {},
            onHiatusReturnToggleClick = {},
        )
    }
}
