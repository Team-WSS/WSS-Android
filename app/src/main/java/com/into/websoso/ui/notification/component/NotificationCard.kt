package com.into.websoso.ui.notification.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.ui.component.AdaptationImage
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Primary20
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.domain.model.NotificationType
import com.into.websoso.ui.notification.model.NotificationUiModel

private const val MAX_NOTIFICATION_LINES = 1

@Composable
fun NotificationCard(
    notification: NotificationUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(if (notification.isRead) White else Primary20)
            .padding(20.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        AdaptationImage(
            imageUrl = notification.notificationIconImage,
            modifier = Modifier
                .size(36.dp)
                .clip(shape = RoundedCornerShape(12.dp)),
        )
        Column(
            modifier = Modifier.padding(start = 14.dp),
        ) {
            Text(
                text = notification.notificationTitle,
                style = WebsosoTheme.typography.title2,
                color = Black,
                maxLines = MAX_NOTIFICATION_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = notification.notificationDescription,
                style = WebsosoTheme.typography.body5,
                color = Gray200,
                modifier = Modifier
                    .padding(top = 2.dp),
                maxLines = MAX_NOTIFICATION_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = notification.createdDate,
                style = WebsosoTheme.typography.body5,
                color = Gray200,
                modifier = Modifier
                    .padding(top = 14.dp),
            )
        }
    }
}

@Preview
@Composable
private fun NotificationCardPreview() {
    WebsosoTheme {
        NotificationCard(
            notification = NotificationUiModel(
                id = 0,
                notificationType = NotificationType.NOTICE,
                notificationTitle = "Notification Title",
                notificationDescription = "Notification Description",
                notificationIconImage = "",
                createdDate = "2021-01-01",
                isRead = false,
                intrinsicId = 0,
            ),
        )
    }
}
