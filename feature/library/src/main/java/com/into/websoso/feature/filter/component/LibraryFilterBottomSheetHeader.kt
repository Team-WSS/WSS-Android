package com.into.websoso.feature.filter.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_cancel_modal

@Composable
internal fun LibraryFilterBottomSheetHeader(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
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

@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetHeaderPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetHeader(
            onDismissRequest = {},
        )
    }
}
