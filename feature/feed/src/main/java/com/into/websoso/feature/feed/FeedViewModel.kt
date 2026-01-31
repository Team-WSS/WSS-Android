package com.into.websoso.feature.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.feed.repository.FeedRepository
import com.into.websoso.feature.feed.model.FeedOrder
import com.into.websoso.feature.feed.model.FeedTab
import com.into.websoso.feature.feed.model.MyFeedFilter
import com.into.websoso.feature.feed.model.SosoFeedType
import com.into.websoso.feature.feed.model.toFeedUiModel
import com.into.websoso.feed.GetFeedsUseCase
import com.into.websoso.feed.GetMyFeedsUseCase
import com.into.websoso.feed.model.Feed
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
        private val feedRepository: FeedRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(FeedUiState())
        val uiState = _uiState.asStateFlow()

        init {
            fetchNextPage()
        }

        /**
         * 내 피드 정렬 순서 변경 (최신순/오래된순)
         */
        fun updateMyFeedSort(sort: FeedOrder) {
            if (uiState.value.myFeedData.sort == sort) return

            _uiState.update {
                it.copy(myFeedData = FeedSourceData(sort = sort))
            }
            fetchNextPage()
        }

        /**
         * 메인 탭 전환 (내 피드 <-> 소소 피드)
         */
        fun updateTab(tab: FeedTab) {
            _uiState.update { it.copy(selectedTab = tab) }
            if (uiState.value.currentData.feeds
                    .isEmpty()
            ) {
                fetchNextPage()
            }
        }

        /**
         * 소소 피드 내 카테고리 전환 (전체 <-> 추천)
         */
        fun updateSosoCategory(category: SosoFeedType) {
            if (uiState.value.sosoCategory == category) return

            Log.d("123123 카테고리", category.toString())
            _uiState.update { it.copy(sosoCategory = category) }
            if (uiState.value.currentData.feeds
                    .isEmpty()
            ) {
                fetchNextPage()
            }
        }

        /**
         * 페이징 데이터 로드 (무한 스크롤 공통 로직)
         */
        fun fetchNextPage(feedId: Long? = null) {
            val state = uiState.value
            val current = state.currentData
            val lastFeedId = feedId ?: current.lastId

            if (state.loading || !current.isLoadable) return

            _uiState.update { it.copy(loading = true) }

            viewModelScope.launch {
                runCatching {
                    when (state.selectedTab) {
                        FeedTab.MY_FEED -> getMyFeedsUseCase(
                            lastFeedId = lastFeedId,
                            genres = state.currentFilter.selectedGenres.map { it.tag },
                            isVisible = state.currentFilter.isVisible,
                            sortCriteria = state.myFeedData.sort.name
                                .uppercase(),
                            isUnVisible = state.currentFilter.isUnVisible,
                        )

                        FeedTab.SOSO_FEED -> getFeedsUseCase(
                            feedsOption = state.sosoCategory.name.uppercase(),
                            lastFeedId = lastFeedId,
                        )
                    }
                }.onSuccess { result ->
                    _uiState.update { currentState ->
                        val source = currentState.currentData
                        val updatedSource = source.copy(
                            feeds = (source.feeds + result.feeds.map(Feed::toFeedUiModel)).toImmutableList(),
                            lastId = result.feeds.lastOrNull()?.id ?: 0,
                            isLoadable = result.isLoadable,
                        )
                        currentState
                            .updateCurrentSource(updatedData = updatedSource)
                            .copy(loading = false, isRefreshing = false)
                    }
                }.onFailure {
                    _uiState.update { it.copy(loading = false, isRefreshing = false, error = true) }
                }
            }
        }

        /**
         * 당겨서 새로고침 (데이터 초기화 후 재요청)
         */
        fun refresh() {
            _uiState.update { state ->
                val clearedState = when (state.selectedTab) {
                    FeedTab.MY_FEED -> state.copy(myFeedData = FeedSourceData(sort = state.myFeedData.sort))

                    FeedTab.SOSO_FEED -> if (state.sosoCategory == SosoFeedType.ALL) {
                        state.copy(sosoAllData = FeedSourceData())
                    } else {
                        state.copy(sosoRecommendationData = FeedSourceData())
                    }
                }
                clearedState.copy(isRefreshing = true)
            }
            fetchNextPage()
        }

        /**
         * 내 피드 장르/공개여부 필터 적용
         */
        fun applyMyFilter(filter: MyFeedFilter) {
            _uiState.update {
                it.copy(currentFilter = filter, myFeedData = FeedSourceData())
            }
            fetchNextPage()
        }

        /**
         * 좋아요 업데이트 (낙관적 업데이트 미적용, 서버 응답 후 반영)
         */
        fun updateLike(selectedFeedId: Long) {
            val targetFeed = uiState.value.currentData.feeds
                .find { it.id == selectedFeedId } ?: return
            val currentIsLiked = targetFeed.isLiked

            viewModelScope.launch {
                runCatching {
                    feedRepository.saveLike(currentIsLiked, selectedFeedId)
                }.onSuccess {
                    _uiState.update { state ->
                        val nextIsLiked = !currentIsLiked
                        val nextCount =
                            if (nextIsLiked) targetFeed.likeCount + 1 else targetFeed.likeCount - 1

                        val updatedFeeds = state.currentData.feeds
                            .map { feed ->
                                if (feed.id == selectedFeedId) {
                                    feed.copy(
                                        isLiked = nextIsLiked,
                                        likeCount = nextCount,
                                    )
                                } else {
                                    feed
                                }
                            }.toImmutableList()

                        state.updateCurrentSource(state.currentData.copy(feeds = updatedFeeds))
                    }
                }.onFailure {
                    _uiState.update { it.copy(error = true) }
                }
            }
        }

        /**
         * 스포일러 신고 (해당 아이템을 즉시 스포일러 상태로 변경)
         */
        fun updateReportedSpoilerFeed(feedId: Long) {
            viewModelScope.launch {
                runCatching {
                    feedRepository.saveSpoilerFeed(feedId)
                }.onSuccess {
                    _uiState.update { state ->
                        val updatedFeeds = state.currentData.feeds
                            .map { feed ->
                                if (feed.id == feedId) feed.copy(isSpoiler = true) else feed
                            }.toImmutableList()

                        state.updateCurrentSource(state.currentData.copy(feeds = updatedFeeds))
                    }
                }.onFailure {
                    _uiState.update { it.copy(error = true) }
                }
            }
        }

        /**
         * 부적절한 피드 신고 (리스트에서 즉시 제거)
         */
        fun updateReportedImpertinenceFeed(feedId: Long) {
            viewModelScope.launch {
                runCatching {
                    feedRepository.saveImpertinenceFeed(feedId)
                }.onSuccess {
                    _uiState.update { state ->
                        val filteredFeeds =
                            state.currentData.feeds
                                .filter { it.id != feedId }
                                .toImmutableList()
                        state.updateCurrentSource(state.currentData.copy(feeds = filteredFeeds))
                    }
                }.onFailure {
                    _uiState.update { it.copy(error = true) }
                }
            }
        }

        /**
         * 피드 삭제 로직
         */
        fun updateRemovedFeed(feedId: Long) {
            val targetFeed = uiState.value.currentData.feeds
                .find { it.id == feedId } ?: return

            _uiState.update { it.copy(loading = true) }

            viewModelScope.launch {
                runCatching {
                    feedRepository.saveRemovedFeed(
                        feedId = feedId,
                        novelId = targetFeed.novel?.id,
                        content = targetFeed.content,
                    )
                }.onSuccess {
                    _uiState.update { state ->
                        val filteredFeeds =
                            state.currentData.feeds
                                .filter { it.id != feedId }
                                .toImmutableList()
                        val updatedData = state.currentData.copy(feeds = filteredFeeds)
                        state.updateCurrentSource(updatedData).copy(loading = false)
                    }
                }.onFailure {
                    _uiState.update { it.copy(loading = false, error = true) }
                }
            }
        }

        /**
         * 모든 피드 데이터를 초기 상태로 리셋하고 첫 페이지를 다시 불러옴
         */
        fun resetFeedsToInitial() {
            _uiState.update { state ->
                state.copy(
                    myFeedData = FeedSourceData(sort = state.myFeedData.sort),
                    sosoAllData = FeedSourceData(),
                    sosoRecommendationData = FeedSourceData(),
                )
            }

            fetchNextPage(feedId = 0L)
        }
    }
