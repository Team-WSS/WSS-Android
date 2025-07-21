package com.into.websoso.feature.library.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Gray70
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary50
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_cancel_modal
import com.into.websoso.core.resource.R.drawable.ic_library_character
import com.into.websoso.core.resource.R.drawable.ic_library_finished
import com.into.websoso.core.resource.R.drawable.ic_library_material
import com.into.websoso.core.resource.R.drawable.ic_library_reading
import com.into.websoso.core.resource.R.drawable.ic_library_relationship
import com.into.websoso.core.resource.R.drawable.ic_library_reset
import com.into.websoso.core.resource.R.drawable.ic_library_stopped
import com.into.websoso.core.resource.R.drawable.ic_library_vibe
import com.into.websoso.core.resource.R.drawable.ic_library_world_view
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.AttractivePoints.CHARACTER
import com.into.websoso.domain.library.model.AttractivePoints.MATERIAL
import com.into.websoso.domain.library.model.AttractivePoints.RELATIONSHIP
import com.into.websoso.domain.library.model.AttractivePoints.VIBE
import com.into.websoso.domain.library.model.AttractivePoints.WORLDVIEW
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.domain.library.model.ReadStatus.QUIT
import com.into.websoso.domain.library.model.ReadStatus.WATCHED
import com.into.websoso.domain.library.model.ReadStatus.WATCHING

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun LibraryFilterBottomSheet(
    readStatues: Map<ReadStatus, Boolean>,
    attractivePoints: Map<AttractivePoints, Boolean>,
    selectedRating: Float,
    onRatingClick: (rating: Float) -> Unit,
    onReadStatusClick: (ReadStatus) -> Unit,
    onAttractivePointClick: (AttractivePoints) -> Unit,
    onDismissRequest: () -> Unit,
    onResetClick: () -> Unit,
    onFilterSearchClick: () -> Unit,
    sheetState: SheetState,
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
                readStatues = readStatues,
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
            onFilterSearchClick = onFilterSearchClick,
        )
    }
}

@Composable
private fun LibraryFilterBottomSheetHeader(onDismissRequest: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "작품 찾기 필터",
            style = WebsosoTheme.typography.body2,
            color = Gray200,
        )

        Box(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .clickable {
                    onDismissRequest()
                },
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = ic_cancel_modal),
                contentDescription = null,
            )
        }
    }
}

@SuppressLint("ResourceType")
@Composable
private fun LibraryFilterBottomSheetReadStatus(
    readStatues: Map<ReadStatus, Boolean>,
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
            isSelected = readStatues[WATCHING] ?: false,
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .width(1.dp)
                .height(32.dp)
                .background(color = Gray70),
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_finished,
            iconTitle = "봤어요",
            iconSize = 24.dp,
            horizontalPadding = 36.dp,
            onClick = { onReadStatusClick(WATCHED) },
            isSelected = readStatues[WATCHED] ?: false,
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .width(1.dp)
                .height(32.dp)
                .background(color = Gray70),
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_stopped,
            iconTitle = "하차",
            iconSize = 24.dp,
            horizontalPadding = 36.dp,
            onClick = { onReadStatusClick(QUIT) },
            isSelected = readStatues[QUIT] ?: false,
        )
    }
}

@SuppressLint("ResourceType")
@Composable
private fun LibraryFilterBottomSheetAttractivePoints(
    attractivePoints: Map<AttractivePoints, Boolean>,
    onAttractivePointClick: (AttractivePoints) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_world_view,
            iconTitle = "세계관",
            horizontalPadding = 12.dp,
            iconSize = 36.dp,
            isSelected = attractivePoints[WORLDVIEW] ?: false,
            onClick = { onAttractivePointClick(WORLDVIEW) },
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_material,
            iconTitle = "소재",
            horizontalPadding = 12.dp,
            iconSize = 36.dp,
            isSelected = attractivePoints[MATERIAL] ?: false,
            onClick = { onAttractivePointClick(MATERIAL) },
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_character,
            iconTitle = "캐릭터",
            horizontalPadding = 12.dp,
            iconSize = 36.dp,
            isSelected = attractivePoints[CHARACTER] ?: false,
            onClick = { onAttractivePointClick(CHARACTER) },
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_relationship,
            iconTitle = "관계",
            horizontalPadding = 12.dp,
            iconSize = 36.dp,
            isSelected = attractivePoints[RELATIONSHIP] ?: false,
            onClick = { onAttractivePointClick(RELATIONSHIP) },
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_vibe,
            iconTitle = "분위기",
            horizontalPadding = 12.dp,
            iconSize = 36.dp,
            isSelected = attractivePoints[VIBE] ?: false,
            onClick = { onAttractivePointClick(VIBE) },
        )
    }
}

@Composable
private fun LibraryFilterBottomSheetButtons(
    onResetClick: () -> Unit,
    onFilterSearchClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
            modifier = Modifier
                .fillMaxHeight()
                .background(color = Gray50)
                .clickable { onResetClick() }
                .padding(
                    vertical = 20.dp,
                    horizontal = 34.dp,
                ),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = ic_library_reset),
                contentDescription = null,
                modifier = Modifier.size(size = 14.dp),
            )
            Text(
                text = "초기화",
                style = WebsosoTheme.typography.title2,
                color = Gray300,
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .background(color = Primary100)
                .clickable { onFilterSearchClick() }
                .padding(vertical = 20.dp)
                .weight(weight = 1f),
        ) {
            Text(
                text = "작품 찾기",
                style = WebsosoTheme.typography.title2,
                color = White,
            )
        }
    }
}

@Composable
private fun LibraryFilterBottomSheetNovelRatingGrid(
    selectedRating: Float,
    onRatingClick: (rating: Float) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            NovelRatingItem(
                title = "3.5 이상",
                modifier = Modifier
                    .weight(weight = 1f)
                    .clickable {
                        onRatingClick(3.5f)
                    },
                isSelected = 3.5f == selectedRating,
            )
            NovelRatingItem(
                title = "4.0 이상",
                modifier = Modifier
                    .weight(weight = 1f)
                    .clickable {
                        onRatingClick(4.0f)
                    },
                isSelected = 4.0f == selectedRating,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            NovelRatingItem(
                title = "4.5 이상",
                modifier = Modifier
                    .weight(weight = 1f)
                    .clickable {
                        onRatingClick(4.5f)
                    },
                isSelected = 4.5f == selectedRating,
            )
            NovelRatingItem(
                title = "4.8 이상",
                modifier = Modifier
                    .weight(weight = 1f)
                    .clickable {
                        onRatingClick(4.8f)
                    },
                isSelected = 4.8f == selectedRating,
            )
        }
    }
}

@Composable
private fun NovelRatingItem(
    title: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    val backgroundColor = if (isSelected) Primary50 else Gray50

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(size = 8.dp),
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = Primary100,
                        shape = RoundedCornerShape(size = 8.dp),
                    )
                } else {
                    Modifier
                },
            )
            .padding(vertical = 14.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = WebsosoTheme.typography.body2,
            color = if (isSelected) Primary100 else Gray300,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheet(
            onDismissRequest = {},
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded,
                skipHiddenState = false,
            ),
            onReadStatusClick = {},
            onAttractivePointClick = {},
            attractivePoints = mapOf(),
            readStatues = mapOf(),
            selectedRating = 3.5f,
            onRatingClick = { },
            onResetClick = { },
            onFilterSearchClick = { },
        )
    }
}
