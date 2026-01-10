package com.into.websoso.feature.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray80
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R
import com.into.websoso.feature.feed.component.FeedFilterChip
import com.into.websoso.feature.feed.component.FeedSection
import com.into.websoso.feature.feed.model.FeedOrder
import com.into.websoso.feature.feed.model.FeedTab
import com.into.websoso.feature.feed.model.SosoFeedType

@Composable
internal fun FeedScreen(
    uiState: FeedUiState,
    onTabSelected: (tab: FeedTab) -> Unit,
    onSortSelected: (order: FeedOrder) -> Unit,
    onSosoTypeSelected: (feedType: SosoFeedType) -> Unit,
    onWriteClick: () -> Unit,
) {
    Scaffold(containerColor = White) { padding ->
        Column {
            FeedTabRow(
                selectedTab = uiState.selectedTab,
                onTabClick = onTabSelected,
                onWriteClick = onWriteClick,
            )

            Spacer(modifier = Modifier.height(height = 2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 12.dp,
                        horizontal = 20.dp,
                    ),
            ) {
                when (uiState.selectedTab) {
                    FeedTab.MY_FEED -> {
                        FeedFilterChip(
                            label = "${uiState.myFeedData.feeds.size}개의 기록",
                            isSelected = true,
                            rightIcon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_drop_down_fill),
                                    contentDescription = null,
                                    tint = Gray80,
                                )
                            },
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_switch),
                                contentDescription = null,
                                tint = Gray80,
                            )

                            Text(
                                text = uiState.myFeedData.sort.title,
                                style = WebsosoTheme.typography.body3,
                                color = Gray300,
                                modifier = Modifier.clickable {
                                    onSortSelected(
                                        when (uiState.myFeedData.sort) {
                                            FeedOrder.NEWEST -> FeedOrder.OLDEST
                                            FeedOrder.OLDEST -> FeedOrder.NEWEST
                                        },
                                    )
                                },
                            )
                        }
                    }

                    FeedTab.SOSO_FEED -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(space = 6.dp)) {
                            FeedFilterChip(
                                isSelected = uiState.sosoCategory == SosoFeedType.ALL,
                                label = SosoFeedType.ALL.title,
                                modifier = Modifier.clickable { onSosoTypeSelected(SosoFeedType.ALL) },
                            )

                            FeedFilterChip(
                                isSelected = uiState.sosoCategory == SosoFeedType.RECOMMENDATION,
                                label = SosoFeedType.RECOMMENDATION.title,
                                leftIcon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_hot),
                                        contentDescription = null,
                                    )
                                },
                                modifier = Modifier.clickable { onSosoTypeSelected(SosoFeedType.RECOMMENDATION) },
                            )
                        }
                    }
                }
            }

            FeedSection(
                feeds = when (uiState.selectedTab) {
                    FeedTab.MY_FEED -> uiState.myFeedData.feeds

                    FeedTab.SOSO_FEED -> when (uiState.sosoCategory) {
                        SosoFeedType.ALL -> uiState.sosoAllData.feeds
                        SosoFeedType.RECOMMENDATION -> uiState.sosoRecommendationData.feeds
                    }
                },
            )
        }
    }
}

@Composable
private fun FeedTabRow(
    selectedTab: FeedTab,
    onTabClick: (tab: FeedTab) -> Unit,
    onWriteClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(end = 20.dp),
    ) {
        SecondaryScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = White,
            edgePadding = 0.dp,
            divider = {},
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        selectedTabIndex = selectedTab.ordinal,
                        matchContentSize = true,
                    ),
                    height = 2.dp,
                    color = Black,
                )
            },
            modifier = Modifier.weight(weight = 1f),
        ) {
            FeedTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { onTabClick(tab) },
                    text = {
                        Text(
                            text = tab.title,
                            style = WebsosoTheme.typography.headline1,
                        )
                    },
                    selectedContentColor = Black,
                    unselectedContentColor = Gray100,
                )
            }
        }

        Box(
            modifier = Modifier
                .clickable { onWriteClick() }
                .padding(vertical = 20.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_pencil_sm),
                contentDescription = null,
                tint = Black,
            )
        }
    }
}

@Preview
@Composable
private fun FeedScreenPreview() {
    WebsosoTheme {
        FeedScreen(
            uiState = FeedUiState(),
            onWriteClick = { },
            onTabSelected = { },
            onSortSelected = { },
            onSosoTypeSelected = { },
        )
    }
}
