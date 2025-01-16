package com.into.websoso.ui.notice.component

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.ui.component.AdaptationImage
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Primary20
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.domain.model.Notice
import com.into.websoso.domain.model.NoticeType

@Composable
fun NoticeCard(
    notice: Notice,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(if (notice.isRead) White else Primary20)
            .padding(20.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        AdaptationImage(
            imageUrl = notice.noticeIconImage,
            modifier = Modifier
                .size(36.dp)
                .clip(shape = RoundedCornerShape(12.dp)),
        )
        Column(
            modifier = Modifier.padding(start = 14.dp),
        ) {
            Text(
                text = notice.noticeTitle,
                style = WebsosoTheme.typography.title2,
                color = Black,
            )
            Text(
                text = notice.noticeDescription,
                style = WebsosoTheme.typography.body5,
                color = Gray200,
                modifier = Modifier
                    .padding(top = 2.dp),
            )
            Text(
                text = notice.createdDate,
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
private fun NoticeCardPreview() {
    WebsosoTheme {
        NoticeCard(
            notice = Notice(
                id = 0,
                noticeType = NoticeType.NOTICE,
                noticeTitle = "Notice Title",
                noticeDescription = "Notice Description",
                noticeIconImage = "",
                createdDate = "2021-01-01",
                isRead = false,
                intrinsicId = 0,
            ),
        )
    }
}
