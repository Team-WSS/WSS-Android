package com.into.websoso.feature.collection.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_navigate_left

@Composable
internal fun CollectionAppBar(
    title: String? = null,
    actionLabel: String? = null,
    onNavigateBack: () -> Unit,
    isActionEnabled: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(White),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 6.dp)
                .size(44.dp),
        ) {
            Image(
                painter = painterResource(id = ic_navigate_left),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }
        title?.let {
            Text(
                text = it,
                color = Black,
                style = WebsosoTheme.typography.title2,
            )
        }
        actionLabel?.let {
            Text(
                text = it,
                color = if (isActionEnabled) Primary100 else Gray100,
                style = WebsosoTheme.typography.title2,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 20.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionAppBarPreview() {
    WebsosoTheme {
        CollectionAppBar(
            title = "작품 리스트",
            actionLabel = "완료",
            onNavigateBack = {},
        )
    }
}
