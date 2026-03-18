package com.into.websoso.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.feed.repository.UpdatedFeedRepository
import com.into.websoso.feature.feed.model.FeedOrder
import com.into.websoso.feature.feed.model.FeedTab
import com.into.websoso.feature.feed.model.MyFeedFilter
import com.into.websoso.feature.feed.model.SosoFeedType
import com.into.websoso.feature.feed.model.toFeedUiModel
import com.into.websoso.feed.GetFeedsUseCase
import com.into.websoso.feed.GetMyFeedsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel
    @Inject
    constructor(
        private val getFeedsUseCase: GetFeedsUseCase,
        private val getMyFeedsUseCase: GetMyFeedsUseCase,
        private val feedRepository: UpdatedFeedRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(FeedUiState())
        val uiState = _uiState.asStateFlow()

        init {
            collectFeedRefreshEvent()
            collectFeedFlows()
            fetchNextPage()
        }

        private fun collectFeedRefreshEvent() {
            viewModelScope.launch {
                feedRepository.feedRefreshEvent.collect {
                    refresh()
                }
            }
        }

        private fun collectFeedFlows() {
            viewModelScope.launch {
                getFeedsUseCase.sosoAllFlow.collect { feeds ->
                    val uiFeeds = feeds.map { it.toFeedUiModel() }.toImmutableList()
                    _uiState.update { state ->
                        val updatedAllData = state.sosoAllData.copy(feeds = uiFeeds)
                        if (state.selectedTab == FeedTab.SOSO_FEED && state.sosoCategory == SosoFeedType.ALL) {
                            state.copy(sosoAllData = updatedAllData).updateCurrentSource(updatedAllData)
                        } else {
                            state.copy(sosoAllData = updatedAllData)
                        }
                    }
                }
            }
            viewModelScope.launch {
                getFeedsUseCase.sosoRecommendedFlow.collect { feeds ->
                    val uiFeeds = feeds.map { it.toFeedUiModel() }.toImmutableList()
                    _uiState.update { state ->
                        val updatedRecData = state.sosoRecommendationData.copy(feeds = uiFeeds)
                        if (state.selectedTab == FeedTab.SOSO_FEED && state.sosoCategory == SosoFeedType.RECOMMENDED) {
                            state
                                .copy(sosoRecommendationData = updatedRecData)
                                .updateCurrentSource(updatedRecData)
                        } else {
                            state.copy(sosoRecommendationData = updatedRecData)
                        }
                    }
                }
            }
            viewModelScope.launch {
                getMyFeedsUseCase.myFeedsFlow.collect { feeds ->
                    val uiFeeds = feeds.map { it.toFeedUiModel() }.toImmutableList()
                    _uiState.update { state ->
                        val updatedMyData = state.myFeedData.copy(feeds = uiFeeds)
                        if (state.selectedTab == FeedTab.MY_FEED) {
                            state.copy(myFeedData = updatedMyData).updateCurrentSource(updatedMyData)
                        } else {
                            state.copy(myFeedData = updatedMyData)
                        }
                    }
                }
            }
        }

        fun fetchNextPage(feedId: Long? = null) {
            val state = uiState.value
            val current = state.currentData
            val lastFeedId = feedId ?: current.lastId

            val tab = state.selectedTab
            val category = state.sosoCategory

            if (state.loading || (!current.isLoadable && lastFeedId != 0L)) return

            _uiState.update { it.copy(loading = true) }

            viewModelScope.launch {
                runCatching {
                    when (tab) {
                        FeedTab.MY_FEED -> {
                            getMyFeedsUseCase(
                                lastFeedId = lastFeedId,
                                genres = state.currentFilter.selectedGenres.map { it.tag },
                                isVisible = state.currentFilter.isVisible,
                                sortCriteria = state.myFeedData.sort.name
                                    .uppercase(),
                                isUnVisible = state.currentFilter.isUnVisible,
                            )
                        }

                        FeedTab.SOSO_FEED -> {
                            getFeedsUseCase(
                                feedsOption = category.name.uppercase(),
                                lastFeedId = lastFeedId,
                            )
                        }
                    }
                }.onSuccess { result ->
                    _uiState.update { currentState ->
                        val newLastId = if (result.isLoadable) {
                            result.feeds.lastOrNull()?.id ?: 0L
                        } else {
                            currentState.currentData.lastId
                        }

                        when (tab) {
                            FeedTab.MY_FEED -> currentState.copy(
                                myFeedData = currentState.myFeedData.copy(
                                    totalCount = result.totalCount,
                                    lastId = if (result.isLoadable) {
                                        currentState.myFeedData.feeds
                                            .lastOrNull()
                                            ?.id
                                            ?: 0L
                                    } else {
                                        currentState.myFeedData.lastId
                                    },
                                    isLoadable = result.isLoadable,
                                ),
                                loading = false,
                                isRefreshing = false,
                            )

                            FeedTab.SOSO_FEED -> if (category == SosoFeedType.ALL) {
                                currentState.copy(
                                    sosoAllData = currentState.sosoAllData.copy(
                                        lastId = newLastId,
                                        isLoadable = result.isLoadable,
                                    ),
                                    loading = false,
                                    isRefreshing = false,
                                )
                            } else {
                                currentState.copy(
                                    sosoRecommendationData = currentState.sosoRecommendationData.copy(
                                        lastId = newLastId,
                                        isLoadable = result.isLoadable,
                                    ),
                                    loading = false,
                                    isRefreshing = false,
                                )
                            }
                        }
                    }
                }.onFailure {
                    _uiState.update { it.copy(loading = false, isRefreshing = false, error = true) }
                }
            }
        }

        /**
         * [새로고침] 기존 데이터를 지우지 않고 isRefreshing만 켠 후 재요청
         * 데이터 교체는 Repository가 Flow를 방출할 때 자연스럽게 이루어짐
         */
        fun refresh() {
            _uiState.update { it.copy(isRefreshing = true) }
            fetchNextPage(feedId = 0L)
        }

        fun updateLike(selectedFeedId: Long) = feedRepository.toggleLikeLocal(selectedFeedId)

        fun updateRemovedFeed(feedId: Long) {
            _uiState.update { it.copy(loading = true) }
            viewModelScope.launch {
                feedRepository.saveRemovedFeed(feedId)
                _uiState.update { it.copy(loading = false) }
            }
        }

        fun updateReportedSpoilerFeed(feedId: Long) {
            viewModelScope.launch { feedRepository.saveSpoilerFeed(feedId) }
        }

        fun updateReportedImpertinenceFeed(feedId: Long) {
            viewModelScope.launch { feedRepository.saveImpertinenceFeed(feedId) }
        }

        override fun onCleared() {
            super.onCleared()
            feedRepository.syncDirtyFeeds()
        }

        // --- 기타 탭/필터 로직 ---
        fun updateMyFeedSort(sort: FeedOrder) {
            if (uiState.value.myFeedData.sort == sort) return
            _uiState.update { it.copy(myFeedData = FeedSourceData(sort = sort)) }
            fetchNextPage(feedId = 0L)
        }

        fun updateTab(tab: FeedTab) {
            _uiState.update { it.copy(selectedTab = tab) }
            if (uiState.value.currentData.feeds
                    .isEmpty()
            ) {
                fetchNextPage(feedId = 0L)
            }
        }

        fun updateSosoCategory(category: SosoFeedType) {
            if (uiState.value.sosoCategory == category) return
            _uiState.update { it.copy(sosoCategory = category) }
            if (uiState.value.currentData.feeds
                    .isEmpty()
            ) {
                fetchNextPage(feedId = 0L)
            }
        }

        fun applyMyFilter(filter: MyFeedFilter) {
            _uiState.update { it.copy(currentFilter = filter, myFeedData = FeedSourceData()) }
            fetchNextPage(feedId = 0L)
        }
    }
