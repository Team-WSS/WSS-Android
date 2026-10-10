package com.into.websoso.feature.library.filter.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.domain.library.model.SeriesStatus
import com.into.websoso.domain.library.model.SeriesStatuses

@Composable
internal fun LibraryFilterBottomSheetSeriesStatus(
    seriesStatuses: SeriesStatuses,
    onSeriesStatusClick: (SeriesStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SeriesStatus.entries.forEach { status ->
            LibraryFilterStatusChip(
                label = status.label,
                isSelected = seriesStatuses[status],
                onClick = { onSeriesStatusClick(status) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetSeriesStatusPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetSeriesStatus(
            seriesStatuses = SeriesStatuses(),
            onSeriesStatusClick = {},
        )
    }
}
