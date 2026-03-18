package com.into.websoso.feature.feed

import com.into.websoso.feature.feed.model.FeedOrder
import com.into.websoso.feature.feed.model.FeedTab
import com.into.websoso.feature.feed.model.FeedUiModel
import com.into.websoso.feature.feed.model.MyFeedFilter
import com.into.websoso.feature.feed.model.SosoFeedType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class FeedUiState(
    val currentFilter: MyFeedFilter = MyFeedFilter(),
    val selectedTab: FeedTab = FeedTab.MY_FEED,
    val sosoCategory: SosoFeedType = SosoFeedType.ALL,
    val myFeedData: FeedSourceData = FeedSourceData(),
    val sosoAllData: FeedSourceData = FeedSourceData(),
    val sosoRecommendationData: FeedSourceData = FeedSourceData(),
    val loading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: Boolean = false,
) {
    val currentData: FeedSourceData
        get() = when (selectedTab) {
            FeedTab.MY_FEED -> myFeedData
            FeedTab.SOSO_FEED -> if (sosoCategory == SosoFeedType.ALL) sosoAllData else sosoRecommendationData
        }

    fun updateCurrentSource(updatedData: FeedSourceData): FeedUiState =
        when (selectedTab) {
            FeedTab.MY_FEED -> {
                copy(myFeedData = updatedData)
            }

            FeedTab.SOSO_FEED -> {
                if (sosoCategory == SosoFeedType.ALL) {
                    copy(sosoAllData = updatedData)
                } else {
                    copy(sosoRecommendationData = updatedData)
                }
            }
        }
}

data class FeedSourceData(
    val feeds: ImmutableList<FeedUiModel> = persistentListOf(),
    val totalCount: Int = 0,
    val lastId: Long = 0,
    val isLoadable: Boolean = true,
    internal val sort: FeedOrder = FeedOrder.RECENT,
)
