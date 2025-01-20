package com.into.websoso.ui.notificationDetail.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.data.model.NotificationDetailEntity
import com.into.websoso.ui.notificationDetail.model.NotificationDetailUiState

@Composable
fun NotificationDetailBody(
    uiState: NotificationDetailUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = modifier
                .padding(20.dp),
        ) {
            Text(
                text = uiState.noticeDetail.notificationTitle,
                style = WebsosoTheme.typography.headline1,
                color = Black,
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = uiState.noticeDetail.notificationCreatedDate,
                style = WebsosoTheme.typography.body5,
                color = Gray200,
                textAlign = TextAlign.Start,
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Gray50)
        HyperlinkText(
            uiState.noticeDetail.notificationDetail,
            style =
                WebsosoTheme.typography.body2,
            textColor = Black,
            modifier = Modifier
                .padding(top = 24.dp, start = 20.dp, end = 20.dp)
                .fillMaxSize(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationDetailBodyPreview() {
    val uiState = NotificationDetailUiState(
        noticeDetail = NotificationDetailEntity.DEFAULT,
    )

    WebsosoTheme {
        NotificationDetailBody(
            uiState,
        )
    }
}
