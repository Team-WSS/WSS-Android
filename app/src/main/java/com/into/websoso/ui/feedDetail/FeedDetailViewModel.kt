package com.into.websoso.ui.feedDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.ui.feedDetail.model.FeedDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Deprecated("피드 QA 완료 후 제거 예정")
@HiltViewModel
class FeedDetailViewModel
    @Inject
    constructor(
        private val feedRepository: FeedRepository,
    ) : ViewModel() {
        private var feedId: Long = -1
        private val _feedDetailUiState: MutableLiveData<FeedDetailUiState> =
            MutableLiveData(FeedDetailUiState())
        val feedDetailUiState: LiveData<FeedDetailUiState> get() = _feedDetailUiState
        var commentId: Long = -1
            private set

        fun updateReportedSpoilerFeed() {
            feedDetailUiState.value?.let { feedDetailUiState ->
                viewModelScope.launch {
                    _feedDetailUiState.value = feedDetailUiState.copy(loading = true)
                    runCatching {
                        feedRepository.saveSpoilerFeed(feedId)
                    }.onSuccess {
                        _feedDetailUiState.value = feedDetailUiState.copy(loading = false)
                    }.onFailure {
                        _feedDetailUiState.value = feedDetailUiState.copy(
                            loading = false,
                            isReported = true,
                        )
                    }
                }
            }
        }

        fun updateReportedImpertinenceFeed() {
            feedDetailUiState.value?.let { feedDetailUiState ->
                viewModelScope.launch {
                    _feedDetailUiState.value = feedDetailUiState.copy(loading = true)
                    runCatching {
                        feedRepository.saveImpertinenceFeed(feedId)
                    }.onSuccess {
                        _feedDetailUiState.value = feedDetailUiState.copy(loading = false)
                    }.onFailure {
                        _feedDetailUiState.value = feedDetailUiState.copy(
                            loading = false,
                            isReported = true,
                        )
                    }
                }
            }
        }
    }
