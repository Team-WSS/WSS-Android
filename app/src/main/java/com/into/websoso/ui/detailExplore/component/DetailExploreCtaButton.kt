package com.into.websoso.ui.detailExplore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.string.detail_explore_search_novel

@Composable
fun DetailExploreCtaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val singleEventHandler = remember { SingleEventHandler.from() }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 8.dp, vertical = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Primary100)
                .clickableWithoutRipple { singleEventHandler.throttleFirst(event = onClick) }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(detail_explore_search_novel),
                style = WebsosoTheme.typography.title1,
                color = White,
            )
        }
    }
}