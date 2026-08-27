package com.into.websoso.ui.novelNotification.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale.Companion.FillHeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_notification_back
import com.into.websoso.core.resource.R.string.novel_notification_delete
import com.into.websoso.core.resource.R.string.novel_notification_edit
import com.into.websoso.domain.model.NovelNotificationType
import com.into.websoso.domain.model.NovelNotificationType.COMPLETION
import com.into.websoso.ui.novelNotification.novelNotificationTitleRes

@Composable
fun NovelNotificationListAppBar(
    notificationType: NovelNotificationType,
    isEditing: Boolean,
    isDeletable: Boolean,
    isActionVisible: Boolean,
    onBackButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(White)
            .fillMaxWidth()
            .padding(start = 6.dp, end = 20.dp)
            .height(44.dp),
        verticalAlignment = CenterVertically,
    ) {
        Image(
            painter = painterResource(id = ic_notification_back),
            contentDescription = null,
            contentScale = FillHeight,
            modifier = Modifier
                .size(44.dp)
                .clickableWithoutRipple { onBackButtonClick() },
        )
        Text(
            text = stringResource(notificationType.novelNotificationTitleRes()),
            style = WebsosoTheme.typography.title2,
            color = Black,
            textAlign = Center,
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp),
        )
        if (isActionVisible) {
            NovelNotificationListAppBarAction(
                isEditing = isEditing,
                isDeletable = isDeletable,
                onEditButtonClick = onEditButtonClick,
                onDeleteButtonClick = onDeleteButtonClick,
            )
        }
    }
}

@Composable
private fun NovelNotificationListAppBarAction(
    isEditing: Boolean,
    isDeletable: Boolean,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
) {
    when (isEditing) {
        true -> Text(
            text = stringResource(novel_notification_delete),
            style = WebsosoTheme.typography.title2,
            color = if (isDeletable) Gray300 else Gray200,
            modifier = Modifier.clickableWithoutRipple {
                if (isDeletable) onDeleteButtonClick()
            },
        )

        false -> Text(
            text = stringResource(novel_notification_edit),
            style = WebsosoTheme.typography.title2,
            color = Gray300,
            modifier = Modifier.clickableWithoutRipple { onEditButtonClick() },
        )
    }
}

@Preview
@Composable
private fun NovelNotificationListAppBarPreview() {
    WebsosoTheme {
        NovelNotificationListAppBar(
            notificationType = COMPLETION,
            isEditing = false,
            isDeletable = false,
            isActionVisible = true,
            onBackButtonClick = {},
            onEditButtonClick = {},
            onDeleteButtonClick = {},
        )
    }
}
