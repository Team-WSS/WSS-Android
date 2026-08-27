package com.into.websoso.ui.novelNotification.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_novel_notification_selected
import com.into.websoso.core.resource.R.drawable.ic_novel_notification_unselected
import com.into.websoso.core.resource.R.string.novel_notification_registered_date
import com.into.websoso.ui.novelNotification.model.NovelNotificationSubscriptionModel

@Composable
fun NovelNotificationSubscriptionItem(
    subscription: NovelNotificationSubscriptionModel,
    isEditing: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = CenterVertically,
    ) {
        AsyncImage(
            model = subscription.novelImage,
            contentDescription = null,
            contentScale = Crop,
            modifier = Modifier
                .width(78.dp)
                .height(105.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = subscription.novelTitle,
                style = WebsosoTheme.typography.title3,
                color = Black,
                maxLines = 1,
                overflow = Ellipsis,
            )
            Text(
                text = subscription.novelAuthor,
                style = WebsosoTheme.typography.body5Secondary,
                color = Gray200,
                maxLines = 1,
                overflow = Ellipsis,
            )
            Text(
                text = stringResource(
                    novel_notification_registered_date,
                    subscription.registeredDate,
                ),
                style = WebsosoTheme.typography.body5,
                color = Gray200,
                maxLines = 1,
            )
        }
        when (isEditing) {
            true -> Image(
                imageVector = ImageVector.vectorResource(
                    id = when (isSelected) {
                        true -> ic_novel_notification_selected
                        false -> ic_novel_notification_unselected
                    },
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )

            false -> Spacer(modifier = Modifier.size(24.dp))
        }
    }
}

@Preview
@Composable
private fun NovelNotificationSubscriptionItemPreview() {
    WebsosoTheme {
        NovelNotificationSubscriptionItem(
            subscription = NovelNotificationSubscriptionModel(
                subscriptionId = 1,
                novelId = 1,
                novelTitle = "여주인공의 이해를 돕기 위하여",
                novelAuthor = "이보라",
                novelImage = "",
                registeredDate = "2026.07.04",
            ),
            isEditing = true,
            isSelected = true,
        )
    }
}
