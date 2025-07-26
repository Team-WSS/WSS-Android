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
import com.into.websoso.core.resource.R.drawable.ic_library_character
import com.into.websoso.core.resource.R.drawable.ic_library_material
import com.into.websoso.core.resource.R.drawable.ic_library_relationship
import com.into.websoso.core.resource.R.drawable.ic_library_vibe
import com.into.websoso.core.resource.R.drawable.ic_library_world_view
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.AttractivePoints.CHARACTER
import com.into.websoso.domain.library.model.AttractivePoints.MATERIAL
import com.into.websoso.domain.library.model.AttractivePoints.RELATIONSHIP
import com.into.websoso.domain.library.model.AttractivePoints.VIBE
import com.into.websoso.domain.library.model.AttractivePoints.WORLDVIEW

@SuppressLint("ResourceType")
@Composable
internal fun LibraryFilterBottomSheetAttractivePoints(
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

@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetAttractivePointsPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetAttractivePoints(
            attractivePoints = mapOf(),
            onAttractivePointClick = { },
        )
    }
}
