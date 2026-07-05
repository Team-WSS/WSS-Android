package com.into.websoso.feature.library.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary20
import com.into.websoso.core.designsystem.theme.Transparent
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_library_sort_check
import com.into.websoso.domain.library.model.SortCriteria

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun LibrarySortBottomSheet(
    selectedSortCriteria: SortCriteria,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onSortSelected: (SortCriteria) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = White,
        dragHandle = null,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SortCriteria.entries.forEach { criteria ->
                LibrarySortItem(
                    label = criteria.label,
                    isSelected = criteria == selectedSortCriteria,
                    onClick = {
                        onSortSelected(criteria)
                        onDismissRequest()
                    },
                )
            }
        }
    }
}

@Composable
private fun LibrarySortItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(40.dp))
            .background(if (isSelected) Primary20 else Transparent)
            .debouncedClickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Icon(
                imageVector = ImageVector.vectorResource(id = ic_library_sort_check),
                contentDescription = null,
                tint = Primary100,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
                    .size(20.dp),
            )
        }
        Text(
            text = label,
            style = WebsosoTheme.typography.body2,
            color = if (isSelected) Black else Gray200,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun LibrarySortBottomSheetPreview() {
    WebsosoTheme {
        LibrarySortBottomSheet(
            selectedSortCriteria = SortCriteria.TITLE,
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded,
                skipHiddenState = false,
            ),
            onDismissRequest = {},
            onSortSelected = {},
        )
    }
}
