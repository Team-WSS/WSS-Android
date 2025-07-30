package com.into.websoso.feature.library.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetAttractivePoints
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetButtons
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetHeader
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetNovelRatingGrid
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetReadStatus
import com.into.websoso.feature.library.model.LibraryFilterUiState
import com.into.websoso.feature.library.model.RatingLevelUiModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun LibraryFilterBottomSheetScreen(
    filterUiState: LibraryFilterUiState,
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    onAttractivePointClick: (AttractivePoints) -> Unit,
    onReadStatusClick: (ReadStatus) -> Unit,
    onRatingClick: (rating: RatingLevelUiModel) -> Unit,
    onResetClick: () -> Unit,
    onFilterSearchClick: () -> Unit,
) {
    LibraryFilterBottomSheetScreen(
        sheetState = sheetState,
        readStatues = filterUiState.readStatuses,
        attractivePoints = filterUiState.attractivePoints,
        selectedRating = filterUiState.novelRating,
        onDismissRequest = onDismissRequest,
        onAttractivePointClick = onAttractivePointClick,
        onReadStatusClick = onReadStatusClick,
        onRatingClick = onRatingClick,
        onResetClick = onResetClick,
        onFilterSearchClick = onFilterSearchClick,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LibraryFilterBottomSheetScreen(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    readStatues: Map<ReadStatus, Boolean>,
    attractivePoints: Map<AttractivePoints, Boolean>,
    onAttractivePointClick: (AttractivePoints) -> Unit,
    onReadStatusClick: (ReadStatus) -> Unit,
    selectedRating: Float,
    onRatingClick: (rating: RatingLevelUiModel) -> Unit,
    onResetClick: () -> Unit,
    onFilterSearchClick: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(size = 16.dp),
        containerColor = White,
        dragHandle = null,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp,
            ),
        ) {
            LibraryFilterBottomSheetHeader(onDismissRequest = onDismissRequest)
            Text(
                text = "읽기 상태",
                style = WebsosoTheme.typography.title2,
                color = Black,
                modifier = Modifier.padding(vertical = 10.dp),
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            LibraryFilterBottomSheetReadStatus(
                onReadStatusClick = onReadStatusClick,
                readStatuses = readStatues,
            )
            Spacer(modifier = Modifier.height(height = 32.dp))
            Text(
                text = "매력포인트",
                style = WebsosoTheme.typography.title2,
                color = Black,
                modifier = Modifier.padding(vertical = 10.dp),
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            LibraryFilterBottomSheetAttractivePoints(
                onAttractivePointClick = onAttractivePointClick,
                attractivePoints = attractivePoints,
            )
            Spacer(modifier = Modifier.height(height = 32.dp))
            Text(
                text = "별점",
                style = WebsosoTheme.typography.title2,
                color = Black,
                modifier = Modifier.padding(vertical = 10.dp),
            )
            LibraryFilterBottomSheetNovelRatingGrid(
                selectedRating = selectedRating,
                onRatingClick = onRatingClick,
            )
            Spacer(modifier = Modifier.height(height = 76.dp))
        }
        LibraryFilterBottomSheetButtons(
            onResetClick = onResetClick,
            onFilterSearchClick = {
                onDismissRequest()
                onFilterSearchClick()
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetScreen(
            onDismissRequest = {},
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded,
                skipHiddenState = false,
            ),
            filterUiState = LibraryFilterUiState(),
            onAttractivePointClick = { },
            onReadStatusClick = { },
            onRatingClick = { },
            onResetClick = { },
            onFilterSearchClick = { },
        )
    }
}
