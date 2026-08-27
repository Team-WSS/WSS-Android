package com.into.websoso.ui.novelNotification.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Secondary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.string.novel_notification_delete_dialog_cancel
import com.into.websoso.core.resource.R.string.novel_notification_delete_dialog_confirm
import com.into.websoso.core.resource.R.string.novel_notification_delete_dialog_message_multiple
import com.into.websoso.core.resource.R.string.novel_notification_delete_dialog_message_single
import com.into.websoso.core.resource.R.string.novel_notification_delete_dialog_title
import com.into.websoso.ui.novelNotification.model.NovelNotificationSubscriptionModel

@Composable
fun NovelNotificationDeleteDialog(
    selectedSubscriptions: List<NovelNotificationSubscriptionModel>,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    val firstSelectedSubscription = selectedSubscriptions.firstOrNull() ?: return

    Dialog(onDismissRequest = onCancelClick) {
        Column(
            modifier = Modifier
                .background(color = White, shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = CenterHorizontally,
        ) {
            Text(
                text = stringResource(novel_notification_delete_dialog_title),
                style = WebsosoTheme.typography.title1,
                color = Black,
                textAlign = Center,
            )
            Text(
                text = when (selectedSubscriptions.size) {
                    1 -> stringResource(
                        novel_notification_delete_dialog_message_single,
                        firstSelectedSubscription.novelTitle,
                    )

                    else -> stringResource(
                        novel_notification_delete_dialog_message_multiple,
                        firstSelectedSubscription.novelTitle,
                        selectedSubscriptions.size - 1,
                    )
                },
                style = WebsosoTheme.typography.body2,
                color = Gray300,
                textAlign = Center,
                modifier = Modifier.padding(top = 10.dp),
            )
            Row(
                modifier = Modifier.padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                NovelNotificationDeleteDialogButton(
                    text = stringResource(novel_notification_delete_dialog_cancel),
                    textColor = Gray300,
                    backgroundColor = Gray50,
                    onClick = onCancelClick,
                )
                NovelNotificationDeleteDialogButton(
                    text = stringResource(novel_notification_delete_dialog_confirm),
                    textColor = White,
                    backgroundColor = Secondary100,
                    onClick = onConfirmClick,
                )
            }
        }
    }
}

@Composable
private fun NovelNotificationDeleteDialogButton(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .width(116.dp)
            .height(40.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
            .clickableWithoutRipple { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = CenterVertically,
    ) {
        Text(
            text = text,
            style = WebsosoTheme.typography.body2,
            color = textColor,
        )
    }
}

@Preview
@Composable
private fun NovelNotificationDeleteDialogPreview() {
    WebsosoTheme {
        NovelNotificationDeleteDialog(
            selectedSubscriptions = listOf(
                NovelNotificationSubscriptionModel(
                    subscriptionId = 1,
                    novelId = 1,
                    novelTitle = "여주인공의 이해를 돕기 위하여",
                    novelAuthor = "이보라",
                    novelImage = "",
                    registeredDate = "2026.07.04",
                ),
            ),
            onCancelClick = {},
            onConfirmClick = {},
        )
    }
}
