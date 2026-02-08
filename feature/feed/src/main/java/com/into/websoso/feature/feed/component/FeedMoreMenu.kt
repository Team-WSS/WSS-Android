package com.into.websoso.feature.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Secondary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun FeedMoreMenu(
    isMyFeed: Boolean,
    onDismissRequest: () -> Unit,
    onFirstItemClick: () -> Unit,
    onSecondItemClick: () -> Unit,
) {
    Popup(
        onDismissRequest = onDismissRequest,
        alignment = Alignment.TopEnd,
        properties = PopupProperties(focusable = true),
        offset = IntOffset(x = 0, y = 140),
    ) {
        val contents = if (isMyFeed) persistentListOf("수정하기", "삭제하기")
        else persistentListOf("스포일러 신고", "부적절한 표현 신고")

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Gray50,
                    shape = RoundedCornerShape(size = 12.dp),
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(size = 12.dp),
                )
                .width(width = 180.dp),
        ) {
            contents.forEachIndexed { index, label ->
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    style = WebsosoTheme.typography.body2,
                    color = if (isMyFeed) Black else Secondary100,
                    modifier = Modifier
                        .width(width = 192.dp)
                        .debouncedClickable {
                            if (index == 0) onFirstItemClick() else onSecondItemClick()
                            onDismissRequest()
                        }
                        .padding(vertical = 14.dp),
                )

                if (index < contents.lastIndex) HorizontalDivider(color = Gray50)
            }
        }
    }
}

@Preview
@Composable
private fun FeedMoreMenuPreview() {
    WebsosoTheme {
        FeedMoreMenu(
            isMyFeed = true,
            onDismissRequest = { },
            onFirstItemClick = { },
            onSecondItemClick = { },
        )
    }
}
