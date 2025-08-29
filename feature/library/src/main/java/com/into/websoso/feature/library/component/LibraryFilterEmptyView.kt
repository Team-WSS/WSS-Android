package com.into.websoso.feature.library.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R

@Composable
internal fun LibraryFilterEmptyView() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1.5f))
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_storage_null),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "해당하는 작품이 없어요\n" +
                    "검색의 범위를 더 넓혀보세요",
                style = WebsosoTheme.typography.body1,
                color = Gray200,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.weight(3f))
        }
    }
}
