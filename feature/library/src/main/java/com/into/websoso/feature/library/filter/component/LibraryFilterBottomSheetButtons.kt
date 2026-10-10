package com.into.websoso.feature.library.filter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray60
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_detail_explore_reset
import com.into.websoso.core.resource.R.string.detail_explore_reset
import com.into.websoso.core.resource.R.string.detail_explore_search_novel

@Composable
internal fun LibraryFilterBottomSheetButtons(
    onResetClick: () -> Unit,
    onFilterSearchClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(start = 17.dp, top = 10.dp, end = 15.dp, bottom = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ResetButton(onClick = onResetClick)
            SearchButton(
                onClick = onFilterSearchClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ResetButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)

    Row(
        modifier = Modifier
            .size(width = 87.dp, height = 53.dp)
            .clip(shape)
            .background(White, shape)
            .border(width = 1.dp, color = Gray60, shape = shape)
            .debouncedClickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = ic_detail_explore_reset),
            contentDescription = null,
            tint = Gray200,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = stringResource(detail_explore_reset),
            style = WebsosoTheme.typography.title2,
            color = Gray200,
        )
    }
}

@Composable
private fun SearchButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(53.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Primary100)
            .debouncedClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(detail_explore_search_novel),
            style = WebsosoTheme.typography.title2,
            color = White,
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetButtonsPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetButtons(
            onFilterSearchClick = { },
            onResetClick = { },
        )
    }
}
