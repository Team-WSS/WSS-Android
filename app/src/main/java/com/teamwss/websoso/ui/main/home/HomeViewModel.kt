package com.teamwss.websoso.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.UserInterestFeedMessage
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.ui.main.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
        updateHomeData()
    }

    fun updateHomeData() {
        updatePopularNovels()
        updatePopularFeeds()
        updateUserInterestFeeds()
        updateRecommendedNovelsByUser()
    }

    private fun updatePopularNovels() {
        viewModelScope.launch {
            runCatching {
                novelRepository.fetchPopularNovels()
            }.onSuccess { popularNovels ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    popularNovels = popularNovels.popularNovels,
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    private fun updatePopularFeeds() {
        viewModelScope.launch {
            runCatching {
                feedRepository.fetchPopularFeeds()
            }.onSuccess { popularFeeds ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    popularFeeds = popularFeeds.popularFeeds.chunked(3),
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    private fun updateUserInterestFeeds() {
        viewModelScope.launch {
            runCatching {
                feedRepository.fetchUserInterestFeeds()
            }.onSuccess { userInterestFeeds ->
                val isInterestNovel =
                    when (UserInterestFeedMessage.fromMessage(userInterestFeeds.message)) {
                        UserInterestFeedMessage.NO_ASSOCIATED_FEEDS -> true
                        UserInterestFeedMessage.NO_INTEREST_NOVELS -> false
                        else -> true
                    }

                _uiState.value = uiState.value?.copy(
                    loading = false,
                    isInterestNovel = isInterestNovel,
                    userInterestFeeds = userInterestFeeds.userInterestFeeds
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true
                )
            }
        }
    }


    private fun updateRecommendedNovelsByUser() {
        viewModelScope.launch {
            runCatching {
                novelRepository.fetchRecommendedNovelsByUserTaste()
            }.onSuccess { recommendedNovelsByUserTaste ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    recommendedNovelsByUserTaste = recommendedNovelsByUserTaste.tasteNovels,
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }
}

