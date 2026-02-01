package com.into.websoso.feature.feed

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray80
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R
import com.into.websoso.feature.feed.component.FeedFilterChip
import com.into.websoso.feature.feed.component.FeedSection
import com.into.websoso.feature.feed.component.MyFeedFilterModal
import com.into.websoso.feature.feed.model.FeedOrder
import com.into.websoso.feature.feed.model.FeedTab
import com.into.websoso.feature.feed.model.MyFeedFilter
import com.into.websoso.feature.feed.model.SosoFeedType

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun FeedScreen(
    uiState: FeedUiState,
    bottomSheetState: SheetState,
    isFilterSheetVisible: Boolean,
    onTabSelected: (tab: FeedTab) -> Unit,
    onSortSelected: (order: FeedOrder) -> Unit,
    onSosoTypeSelected: (feedType: SosoFeedType) -> Unit,
    onWriteClick: () -> Unit,
    onFilterClick: () -> Unit,
    onProfileClick: (userId: Long, feedTab: FeedTab) -> Unit,
    onMoreClick: (feedId: Long) -> Unit,
    onNovelClick: (novelId: Long) -> Unit,
    onLikeClick: (feedId: Long) -> Unit,
    onContentClick: (feedId: Long, isLiked: Boolean) -> Unit,
    onApplyFilterClick: (filter: MyFeedFilter) -> Unit,
    onFilterCloseClick: () -> Unit,
    onFirstItemClick: (feedId: Long, isMyFeed: Boolean) -> Unit,
    onSecondItemClick: (feedId: Long, isMyFeed: Boolean) -> Unit,
    onRefreshPull: () -> Unit,
) {
    Scaffold(containerColor = White) { _ ->
        Column(modifier = Modifier.statusBarsPadding()) {
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
                            modifier = Modifier.debouncedClickable { onFilterClick() },
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
                                modifier = Modifier.debouncedClickable {
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
                                modifier = Modifier.debouncedClickable {
                                    onSosoTypeSelected(SosoFeedType.ALL)
                                },
                            )

                            FeedFilterChip(
                                isSelected = uiState.sosoCategory == SosoFeedType.RECOMMENDED,
                                label = SosoFeedType.RECOMMENDED.title,
                                leftIcon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_hot),
                                        contentDescription = null,
                                    )
                                },
                                modifier = Modifier.debouncedClickable {
                                    onSosoTypeSelected(SosoFeedType.RECOMMENDED)
                                },
                            )
                        }
                    }
                }
            }

            FeedSection(
                currentTab = uiState.selectedTab,
                feeds = when (uiState.selectedTab) {
                    FeedTab.MY_FEED -> uiState.myFeedData.feeds
                    FeedTab.SOSO_FEED -> when (uiState.sosoCategory) {
                        SosoFeedType.ALL -> uiState.sosoAllData.feeds
                        SosoFeedType.RECOMMENDED -> uiState.sosoRecommendationData.feeds
                    }
                },
                onProfileClick = onProfileClick,
                onMoreClick = onMoreClick,
                onNovelClick = onNovelClick,
                onLikeClick = onLikeClick,
                onContentClick = onContentClick,
                onFirstItemClick = onFirstItemClick,
                onSecondItemClick = onSecondItemClick,
                onRefreshPull = onRefreshPull,
                isRefreshing = uiState.isRefreshing,
            )
        }

        if (isFilterSheetVisible) {
            ModalBottomSheet(
                dragHandle = null,
                onDismissRequest = {},
                sheetState = bottomSheetState,
                containerColor = White,
                contentWindowInsets = { WindowInsets(0) },
            ) {
                MyFeedFilterModal(
                    initialFilter = uiState.currentFilter,
                    onApplyFilterClick = onApplyFilterClick,
                    onFilterCloseClick = onFilterCloseClick,
                )
            }
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
            minTabWidth = 0.dp,
            divider = {},
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(
                            selectedTabIndex = selectedTab.ordinal,
                            matchContentSize = false,
                        )
                        .padding(horizontal = 8.dp),
                    height = 2.dp,
                    color = Black,
                )
            },
            modifier = Modifier
                .weight(weight = 1f)
                .padding(horizontal = 12.dp),
        ) {
            FeedTab.entries.forEach { tab ->
                val selected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .debouncedClickable { onTabClick(tab) }
                        .padding(horizontal = 8.dp)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = tab.title,
                        style = WebsosoTheme.typography.headline1,
                        color = if (selected) Black else Gray100,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .debouncedClickable { onWriteClick() }
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun FeedScreenPreview() {
    WebsosoTheme {
        FeedScreen(
            uiState = FeedUiState(),
            bottomSheetState = rememberModalBottomSheetState(),
            isFilterSheetVisible = false,
            onWriteClick = { },
            onTabSelected = { },
            onSortSelected = { },
            onSosoTypeSelected = { },
            onProfileClick = { _, _ -> },
            onMoreClick = { },
            onNovelClick = { },
            onLikeClick = { },
            onContentClick = { _, _ -> },
            onFilterClick = {},
            onApplyFilterClick = {},
            onFilterCloseClick = {},
            onFirstItemClick = { _, _ -> },
            onSecondItemClick = { _, _ -> },
            onRefreshPull = {},
        )
    }
}
