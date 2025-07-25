package com.into.websoso.feature.library.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.WebsosoTheme

@Composable
internal fun LibraryTopBar(onSearchClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "서재",
            style = WebsosoTheme.typography.headline1,
            color = Black,
        )
        /*
        TODO: 추후 검색 기능 구현 시 롤백
                IconButton(onClick = onSearchClick) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = ic_common_search),
                        contentDescription = "검색",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

         */
    }
}
