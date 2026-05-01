package com.into.websoso.ui.detailExplore.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_detail_explore_reset
import com.into.websoso.core.resource.R.drawable.ic_navigate_left
import com.into.websoso.core.resource.R.string.detail_explore_info
import com.into.websoso.core.resource.R.string.detail_explore_keyword
import com.into.websoso.core.resource.R.string.detail_explore_reset
import com.into.websoso.ui.detailExplore.model.SelectedFragmentTitle
import com.into.websoso.ui.detailExplore.model.SelectedFragmentTitle.INFO
import com.into.websoso.ui.detailExplore.model.SelectedFragmentTitle.KEYWORD

@Composable
fun DetailExploreAppBar(
    selectedTab: SelectedFragmentTitle,
    isInfoChipActive: Boolean,
    isKeywordChipActive: Boolean,
    onTabSelected: (SelectedFragmentTitle) -> Unit,
    onBackClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(White)
            .fillMaxWidth()
            .height(44.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .size(44.dp)
                .clickableWithoutRipple(onClick = onBackClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = ic_navigate_left),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }
        TabLabel(
            label = stringResource(detail_explore_info),
            isSelected = selectedTab == INFO,
            isActive = isInfoChipActive,
            slotWidth = 68.dp,
            onClick = { onTabSelected(INFO) },
        )
        TabLabel(
            label = stringResource(detail_explore_keyword),
            isSelected = selectedTab == KEYWORD,
            isActive = isKeywordChipActive,
            slotWidth = 69.dp,
            onClick = { onTabSelected(KEYWORD) },
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickableWithoutRipple(onClick = onResetClick),
        ) {
            Image(
                painter = painterResource(id = ic_detail_explore_reset),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = stringResource(detail_explore_reset),
                style = WebsosoTheme.typography.title2,
                color = Gray300,
            )
        }
    }
}

@Composable
private fun TabLabel(
    label: String,
    isSelected: Boolean,
    isActive: Boolean,
    slotWidth: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(slotWidth)
            .height(30.dp)
            .clickableWithoutRipple(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(IntrinsicSize.Max),
            ) {
                Text(
                    text = label,
                    style = WebsosoTheme.typography.title1,
                    color = if (isSelected) Primary100 else Gray200,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Primary100),
                    )
                }
            }
            Spacer(modifier = Modifier.width(3.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = if (isActive) Primary100 else Color.Transparent,
                        shape = CircleShape,
                    ),
            )
        }
    }
}