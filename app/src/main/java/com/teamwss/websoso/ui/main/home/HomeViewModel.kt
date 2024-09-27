package com.teamwss.websoso.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.PopularFeedsEntity
import com.teamwss.websoso.data.model.PopularNovelsEntity
import com.teamwss.websoso.data.model.RecommendedNovelsByUserTasteEntity
import com.teamwss.websoso.data.model.UserInterestFeedMessage
import com.teamwss.websoso.data.model.UserInterestFeedMessage.NO_INTEREST_NOVELS
import com.teamwss.websoso.data.model.UserInterestFeedsEntity
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.ui.main.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val novelRepository: NovelRepository,
    private val feedRepository: FeedRepository,
) : ViewModel() {

    private val _uiState: MutableLiveData<HomeUiState> = MutableLiveData(HomeUiState())
    val uiState: LiveData<HomeUiState> get() = _uiState

    init {
        updateHome()
    }

    private fun updateHome() {
        viewModelScope.launch {
            _uiState.value = uiState.value?.copy(loading = true)

            runCatching {
                listOf(
                    async { novelRepository.fetchPopularNovels() },
                    async { feedRepository.fetchPopularFeeds() },
                    async { feedRepository.fetchUserInterestFeeds() },
                    async { novelRepository.fetchRecommendedNovelsByUserTaste() }
                ).awaitAll()
            }.onSuccess { responses ->
                val popularNovels = responses[0] as PopularNovelsEntity
                val popularFeeds = responses[1] as PopularFeedsEntity
                val userInterestFeeds = responses[2] as UserInterestFeedsEntity
                val recommendedNovels = responses[3] as RecommendedNovelsByUserTasteEntity

                _uiState.value = uiState.value?.copy(
                    loading = false,
                    popularNovels = popularNovels.popularNovels,
                    popularFeeds = popularFeeds.popularFeeds.chunked(3),
                    isInterestNovel = isUserInterestedInNovels(userInterestFeeds.message),
                    userInterestFeeds = userInterestFeeds.userInterestFeeds,
                    recommendedNovelsByUserTaste = recommendedNovels.tasteNovels
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun updateFeed() {
        viewModelScope.launch {
            runCatching {
                listOf(
                    async { feedRepository.fetchPopularFeeds() },
                    async { feedRepository.fetchUserInterestFeeds() }
                ).awaitAll()
            }.onSuccess { responses ->
                val popularFeeds = responses[0] as PopularFeedsEntity
                val userInterestFeeds = responses[1] as UserInterestFeedsEntity

                _uiState.value = uiState.value?.copy(
                    popularFeeds = popularFeeds.popularFeeds.chunked(3),
                    isInterestNovel = isUserInterestedInNovels(userInterestFeeds.message),
                    userInterestFeeds = userInterestFeeds.userInterestFeeds
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    error = true,
                )
            }
        }
    }

    fun updateNovel() {
        viewModelScope.launch {
            runCatching {
                listOf(
                    async { novelRepository.fetchPopularNovels() },
                    async { novelRepository.fetchRecommendedNovelsByUserTaste() }
                ).awaitAll()
            }.onSuccess { responses ->
                val popularNovels = responses[0] as PopularNovelsEntity
                val recommendedNovels = responses[1] as RecommendedNovelsByUserTasteEntity

                _uiState.value = uiState.value?.copy(
                    popularNovels = popularNovels.popularNovels,
                    recommendedNovelsByUserTaste = recommendedNovels.tasteNovels
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    error = true,
                )
            }
        }
    }

    private fun isUserInterestedInNovels(userInterestFeedMessage: String): Boolean {
        return when (UserInterestFeedMessage.fromMessage(userInterestFeedMessage)) {
            NO_INTEREST_NOVELS -> false
            else -> true
        }
    }
}
