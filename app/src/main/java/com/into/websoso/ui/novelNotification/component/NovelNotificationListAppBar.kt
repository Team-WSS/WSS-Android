package com.into.websoso.ui.novelNotification.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center as CenterAlignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale.Companion.FillHeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
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
import com.into.websoso.domain.model.NovelNotificationType.HIATUS_RETURN
import com.into.websoso.ui.novelNotification.novelNotificationTitleRes

// 뒤로가기(6 + 44)와 액션 영역을 침범하지 않도록 제목이 쓸 수 있는 좌우 여백
private val TITLE_HORIZONTAL_MARGIN = 70.dp

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
    Box(
        modifier = modifier
            .background(White)
            .fillMaxWidth()
            .height(44.dp),
    ) {
        Image(
            painter = painterResource(id = ic_notification_back),
            contentDescription = null,
            contentScale = FillHeight,
            modifier = Modifier
                .align(CenterStart)
                .padding(start = 6.dp)
                .size(44.dp)
                .clickableWithoutRipple { onBackButtonClick() },
        )
        // 뒤로가기/액션 유무와 무관하게 제목이 화면 정중앙에 오도록 Box 위에 겹쳐 배치한다
        Text(
            text = stringResource(notificationType.novelNotificationTitleRes()),
            style = WebsosoTheme.typography.title2,
            color = Black,
            textAlign = Center,
            maxLines = 1,
            overflow = Ellipsis,
            modifier = Modifier
                .align(CenterAlignment)
                .padding(horizontal = TITLE_HORIZONTAL_MARGIN),
        )
        if (isActionVisible) {
            NovelNotificationListAppBarAction(
                isEditing = isEditing,
                isDeletable = isDeletable,
                onEditButtonClick = onEditButtonClick,
                onDeleteButtonClick = onDeleteButtonClick,
                modifier = Modifier
                    .align(CenterEnd)
                    .padding(end = 20.dp),
            )
        }
    }
}

@Composable
private fun BoxScope.NovelNotificationListAppBarAction(
    isEditing: Boolean,
    isDeletable: Boolean,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (isEditing) {
        true -> Text(
            text = stringResource(novel_notification_delete),
            style = WebsosoTheme.typography.title2,
            color = if (isDeletable) Gray300 else Gray200,
            modifier = modifier.clickableWithoutRipple {
                if (isDeletable) onDeleteButtonClick()
            },
        )

        false -> Text(
            text = stringResource(novel_notification_edit),
            style = WebsosoTheme.typography.title2,
            color = Gray300,
            modifier = modifier.clickableWithoutRipple { onEditButtonClick() },
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

@Preview
@Composable
private fun NovelNotificationListAppBarEditingPreview() {
    WebsosoTheme {
        NovelNotificationListAppBar(
            notificationType = COMPLETION,
            isEditing = true,
            isDeletable = false,
            isActionVisible = true,
            onBackButtonClick = {},
            onEditButtonClick = {},
            onDeleteButtonClick = {},
        )
    }
}

@Preview
@Composable
private fun NovelNotificationListAppBarDeletablePreview() {
    WebsosoTheme {
        NovelNotificationListAppBar(
            notificationType = COMPLETION,
            isEditing = true,
            isDeletable = true,
            isActionVisible = true,
            onBackButtonClick = {},
            onEditButtonClick = {},
            onDeleteButtonClick = {},
        )
    }
}

@Preview
@Composable
private fun NovelNotificationListAppBarActionHiddenPreview() {
    WebsosoTheme {
        NovelNotificationListAppBar(
            notificationType = HIATUS_RETURN,
            isEditing = false,
            isDeletable = false,
            isActionVisible = false,
            onBackButtonClick = {},
            onEditButtonClick = {},
            onDeleteButtonClick = {},
        )
    }
}
