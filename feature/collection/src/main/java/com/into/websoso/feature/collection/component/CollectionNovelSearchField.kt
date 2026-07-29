package com.into.websoso.feature.collection.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray70New
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_common_search

@Composable
internal fun CollectionNovelSearchField(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .border(
                width = 1.dp,
                color = Gray70New,
                shape = RoundedCornerShape(14.dp),
            ).padding(
                start = 16.dp,
                end = 10.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "작품 제목, 작가를 검색하세요",
            color = Gray100,
            style = WebsosoTheme.typography.body4,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = ic_common_search),
                contentDescription = null,
                modifier = Modifier.size(width = 25.dp, height = 26.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionNovelSearchFieldPreview() {
    WebsosoTheme {
        CollectionNovelSearchField(modifier = Modifier.padding(20.dp))
    }
}
