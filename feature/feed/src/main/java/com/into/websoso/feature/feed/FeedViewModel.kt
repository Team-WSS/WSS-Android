package com.into.websoso.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.feed.repository.FeedRepository
import com.into.websoso.feature.feed.model.FeedOrder
import com.into.websoso.feature.feed.model.FeedTab
import com.into.websoso.feature.feed.model.SosoFeedType
import com.into.websoso.feature.feed.model.toFeedUiModel
import com.into.websoso.feed.GetFeedsUseCase
import com.into.websoso.feed.model.Feed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedsUseCase: GetFeedsUseCase,
    private val feedRepository: FeedRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Case: 최초 진입 (MyFeed, 최신순)
     */
    init {
        fetchNextPage()
    }

    /**
     * Case: 내 피드 내 정렬 변경 (최신순 <-> 오래된순)
     */
    fun updateMyFeedSort(sort: FeedOrder) {
        if (uiState.value.myFeedData.sort == sort) return

        _uiState.update {
            it.copy(
                myFeedData = FeedSourceData(sort = sort),
            )
        }
        fetchNextPage()
    }

    /**
     * Case: 소소 피드 탭으로 전환 (전체)
     */
    fun updateTab(tab: FeedTab) {
        _uiState.update { it.copy(selectedTab = tab) }

        if (uiState.value.currentData.feeds.isEmpty()) {
            fetchNextPage()
        }
    }

    /**
     * Case: 소소 피드 내 카테고리 전환 (전체 <-> 추천)
     */
    fun updateSosoCategory(category: SosoFeedType) {
        if (uiState.value.sosoCategory == category) return

        _uiState.update { it.copy(sosoCategory = category) }

        if (uiState.value.currentData.feeds.isEmpty()) {
            fetchNextPage()
        }
    }

    /**
     * 무한 스크롤 및 공통 페이징 처리 함수
     */
    fun fetchNextPage() {
        val state = uiState.value
        val current = state.currentData

        if (state.loading || !current.isLoadable) return

        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            runCatching {
                when (state.selectedTab) {
                    FeedTab.MY_FEED -> getFeedsUseCase()
                    FeedTab.SOSO_FEED -> getFeedsUseCase(
                        feedsOption = state.sosoCategory.name.uppercase(),
                        lastFeedId = current.lastId,
                    )
                }
            }.onSuccess { result ->
                _uiState.update { state ->
                    val current = state.currentData

                    val updatedSource = current.copy(
                        feeds = (current.feeds + result.feeds.map(Feed::toFeedUiModel)).toImmutableList(),
                        lastId = result.feeds.last().id,
                        isLoadable = result.isLoadable,
                    )

                    when (state.selectedTab) {
                        FeedTab.MY_FEED -> state.copy(myFeedData = updatedSource, loading = false)
                        FeedTab.SOSO_FEED -> when (state.sosoCategory) {
                            SosoFeedType.ALL -> state.copy(
                                sosoAllData = updatedSource,
                                loading = false,
                            )

                            SosoFeedType.RECOMMENDATION -> state.copy(
                                sosoRecommendationData = updatedSource,
                                loading = false,
                            )
                        }
                    }
                }

            }.onFailure {
                _uiState.update { it.copy(loading = false, error = true) }
            }
        }
    }

    /**
     * 새로고침 (Pull to Refresh 등)
     */
    fun refresh() {
        _uiState.update {
            when (it.selectedTab) {
                FeedTab.MY_FEED -> it.copy(myFeedData = FeedSourceData(sort = it.myFeedData.sort))
                FeedTab.SOSO_FEED -> if (it.sosoCategory == SosoFeedType.ALL) {
                    it.copy(sosoAllData = FeedSourceData())
                } else {
                    it.copy(sosoRecommendationData = FeedSourceData())
                }
            }
        }
        fetchNextPage()
    }

    fun updateLike(selectedFeedId: Long) {
        // 1. 현재 UI 상태에서 해당 피드 찾기
        val currentState = uiState.value
        val targetFeed = currentState.currentData.feeds.find { it.id == selectedFeedId } ?: return

        // 2. 반전될 상태 미리 계산
        val prevIsLiked = targetFeed.isLiked
        val nextIsLiked = !prevIsLiked // 현재 상태의 반대
        val nextLikeCount = if (nextIsLiked) targetFeed.likeCount + 1 else targetFeed.likeCount - 1

        viewModelScope.launch {
            // 서버에 좋아요 상태 저장 요청
            val result = runCatching {
                feedRepository.saveLike(selectedFeedId, nextIsLiked)
            }

            result.onSuccess {
                _uiState.update { state ->
                    // 현재 활성화된 소스(myFeedData, sosoAllData 등) 내의 리스트에서 해당 아이템만 교체
                    val updatedSource = state.currentData.copy(
                        items = state.currentData.items.map { feed ->
                            if (feed.id == selectedFeedId) {
                                feed.copy(isLiked = nextIsLiked, likeCount = nextLikeCount)
                            } else feed
                        }.toImmutableList()
                    )

                    // 현재 어떤 탭/카테고리인지 확인하여 해당 데이터만 업데이트
                    when (state.selectedTab) {
                        FeedTab.MY_FEED -> state.copy(myFeedData = updatedSource)
                        FeedTab.SOSO_FEED -> {
                            if (state.sosoCategory == SosoCategory.ALL) {
                                state.copy(sosoAllData = updatedSource)
                            } else {
                                state.copy(sosoRecoData = updatedSource)
                            }
                        }
                    }
                }
            }.onFailure {
                _uiState.update { it.copy(error = true) }
            }
        }
    }
//
//    fun updateLikedSync(
//        selectedFeedId: Long,
//        isLiked: Boolean,
//        updatedLikeCount: Int,
//    ) {
//        _uiState.value = feedUiState.value.copy(
////            feeds = feedUiState.value.feeds?.map { feedModel ->
////                when (feedModel.id == selectedFeedId) {
////                    true -> feedModel.copy(
////                        isLiked = isLiked,
////                        likeCount = updatedLikeCount,
////                    )
////
////                    false -> feedModel
////                }
////            } ?: emptyList(),
//            isRefreshed = false,
//        )
//    }
//
//    fun updateReportedSpoilerFeed(feedId: Long) {
//        feedUiState.value.let { feedUiState ->
//            viewModelScope.launch {
//                _uiState.value = feedUiState.copy(loading = true)
//                runCatching {
//                    feedRepository.saveSpoilerFeed(feedId)
//                }.onSuccess {
//                    _uiState.value = feedUiState.copy(loading = false)
//                }.onFailure {
//                    _uiState.value = feedUiState.copy(
//                        loading = false,
//                        error = true,
//                    )
//                }
//            }
//        }
//    }
//
//    fun updateReportedImpertinenceFeed(feedId: Long) {
//        feedUiState.value.let { feedUiState ->
//            viewModelScope.launch {
//                _uiState.value = feedUiState.copy(loading = true)
//                runCatching {
//                    feedRepository.saveImpertinenceFeed(feedId)
//                }.onSuccess {
//                    _uiState.value = feedUiState.copy(loading = false)
//                }.onFailure {
//                    _uiState.value = feedUiState.copy(
//                        loading = false,
//                        error = true,
//                    )
//                }
//            }
//        }
//    }
//
//    fun updateRemovedFeed(feedId: Long) {
//        feedUiState.value.let { feedUiState ->
//            val removedFeed = feedUiState.feeds.find { it.id == feedId }
//            viewModelScope.launch {
//                _uiState.value = feedUiState.copy(loading = true)
//                runCatching {
//                    feedRepository.saveRemovedFeed(
//                        feedId = feedId,
//                        novelId = removedFeed?.novel?.id,
//                        content = removedFeed?.content.orEmpty(),
//                    )
//                }.onSuccess {
//                    _uiState.value = feedUiState.copy(
//                        loading = false,
//                        //   feeds = feedUiState.feeds.filter { it.id != feedId },
//                    )
//                }.onFailure {
//                    _uiState.value = feedUiState.copy(
//                        loading = false,
//                        error = true,
//                    )
//                }
//            }
//        }
//    }
}
