package com.into.websoso.feature.filter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_library_reset

@Composable
internal fun LibraryFilterBottomSheetButtons(
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

@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetButtonsPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetButtons(
            onFilterSearchClick = { },
            onResetClick = { },
        )
    }
}
