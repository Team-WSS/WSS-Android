package com.into.websoso.feature.library.filter.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray70
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_library_finished
import com.into.websoso.core.resource.R.drawable.ic_library_reading
import com.into.websoso.core.resource.R.drawable.ic_library_stopped
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.domain.library.model.ReadStatus.QUIT
import com.into.websoso.domain.library.model.ReadStatus.WATCHED
import com.into.websoso.domain.library.model.ReadStatus.WATCHING
import com.into.websoso.domain.library.model.ReadStatuses

@SuppressLint("ResourceType")
@Composable
internal fun LibraryFilterBottomSheetReadStatus(
    readStatuses: ReadStatuses,
    onReadStatusClick: (ReadStatus) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_reading,
            iconTitle = "보는 중",
            iconSize = 24.dp,
            horizontalPadding = 0.dp,
            onClick = { onReadStatusClick(WATCHING) },
            isSelected = readStatuses[WATCHING],
            modifier = Modifier.weight(1f),
        )
        ReadStatusDivider()
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_finished,
            iconTitle = "봤어요",
            iconSize = 24.dp,
            horizontalPadding = 0.dp,
            onClick = { onReadStatusClick(WATCHED) },
            isSelected = readStatuses[WATCHED],
            modifier = Modifier.weight(1f),
        )
        ReadStatusDivider()
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_stopped,
            iconTitle = "하차",
            iconSize = 24.dp,
            horizontalPadding = 0.dp,
            onClick = { onReadStatusClick(QUIT) },
            isSelected = readStatuses[QUIT],
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ReadStatusDivider() {
    Box(
        modifier = Modifier
            .height(32.dp)
            .width(1.dp)
            .background(Gray70),
    )
}

@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetReadStatusPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetReadStatus(
            readStatuses = ReadStatuses(),
            onReadStatusClick = { },
        )
    }
}
