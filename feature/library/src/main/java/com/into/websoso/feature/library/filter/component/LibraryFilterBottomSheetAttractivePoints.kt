package com.into.websoso.feature.library.filter.component

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
import com.into.websoso.domain.library.model.AttractivePoint
import com.into.websoso.domain.library.model.AttractivePoint.CHARACTER
import com.into.websoso.domain.library.model.AttractivePoint.MATERIAL
import com.into.websoso.domain.library.model.AttractivePoint.RELATIONSHIP
import com.into.websoso.domain.library.model.AttractivePoint.VIBE
import com.into.websoso.domain.library.model.AttractivePoint.WORLDVIEW
import com.into.websoso.domain.library.model.AttractivePoints

@Composable
internal fun LibraryFilterBottomSheetAttractivePoints(
    attractivePoints: AttractivePoints,
    onAttractivePointClick: (AttractivePoint) -> Unit,
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
            isSelected = attractivePoints[WORLDVIEW],
            onClick = { onAttractivePointClick(WORLDVIEW) },
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_material,
            iconTitle = "소재",
            horizontalPadding = 12.dp,
            iconSize = 36.dp,
            isSelected = attractivePoints[MATERIAL],
            onClick = { onAttractivePointClick(MATERIAL) },
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_character,
            iconTitle = "캐릭터",
            horizontalPadding = 12.dp,
            iconSize = 36.dp,
            isSelected = attractivePoints[CHARACTER],
            onClick = { onAttractivePointClick(CHARACTER) },
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_relationship,
            iconTitle = "관계",
            horizontalPadding = 12.dp,
            iconSize = 36.dp,
            isSelected = attractivePoints[RELATIONSHIP],
            onClick = { onAttractivePointClick(RELATIONSHIP) },
        )
        LibraryFilterBottomSheetClickableItem(
            icon = ic_library_vibe,
            iconTitle = "분위기",
            horizontalPadding = 12.dp,
            iconSize = 36.dp,
            isSelected = attractivePoints[VIBE],
            onClick = { onAttractivePointClick(VIBE) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetAttractivePointsPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetAttractivePoints(
            attractivePoints = AttractivePoints(),
            onAttractivePointClick = { },
        )
    }
}
