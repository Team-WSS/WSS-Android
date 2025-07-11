package com.into.websoso.feature.filter.component

import android.annotation.SuppressLint
import androidx.annotation.IntegerRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R

@SuppressLint("ResourceType")
@Composable
internal fun LibraryFilterBottomSheetClickableItem(
    onClick: () -> Unit,
    @IntegerRes icon: Int,
    iconTitle: String,
    iconSize: Dp,
    horizontalPadding: Dp,
    isSelected: Boolean = false,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = horizontalPadding),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = null,
            tint = if (isSelected) Primary100 else Gray100,
            modifier = Modifier.size(size = iconSize),
        )
        Spacer(modifier = Modifier.height(height = 6.dp))
        Text(
            text = iconTitle,
            style = WebsosoTheme.typography.body4,
            color = if (isSelected) Primary100 else Gray300,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetClickableItemPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetClickableItem(
            icon = R.drawable.ic_library_vibe,
            iconTitle = "분위기",
            horizontalPadding = 12.dp,
            iconSize = 36.dp,
            isSelected = false,
            onClick = { },
        )
    }
}
