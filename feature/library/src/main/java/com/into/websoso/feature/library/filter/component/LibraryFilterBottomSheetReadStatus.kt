package com.into.websoso.feature.library.filter.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_library_finished
import com.into.websoso.core.resource.R.drawable.ic_library_reading
import com.into.websoso.core.resource.R.drawable.ic_library_stopped
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.domain.library.model.ReadStatus.QUIT
import com.into.websoso.domain.library.model.ReadStatus.WATCHED
import com.into.websoso.domain.library.model.ReadStatus.WATCHING

@SuppressLint("ResourceType")
@Composable
internal fun LibraryFilterBottomSheetReadStatus(
    readStatuses: Map<ReadStatus, Boolean>,
    onReadStatusClick: (ReadStatus) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_reading,
            iconTitle = "보는 중",
            iconSize = 24.dp,
            horizontalPadding = 36.dp,
            onClick = { onReadStatusClick(WATCHING) },
            isSelected = readStatuses[WATCHING] ?: false,
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_finished,
            iconTitle = "봤어요",
            iconSize = 24.dp,
            horizontalPadding = 36.dp,
            onClick = { onReadStatusClick(WATCHED) },
            isSelected = readStatuses[WATCHED] ?: false,
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_stopped,
            iconTitle = "하차",
            iconSize = 24.dp,
            horizontalPadding = 36.dp,
            onClick = { onReadStatusClick(QUIT) },
            isSelected = readStatuses[QUIT] ?: false,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetReadStatusPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetReadStatus(
            readStatuses = mapOf(),
            onReadStatusClick = { },
        )
    }
}
