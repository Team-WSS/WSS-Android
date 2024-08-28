package com.teamwss.websoso.ui.feedDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailModel
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Error
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Loading
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Success
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _feedDetailUiState: MutableLiveData<FeedDetailUiState> = MutableLiveData(Loading)
    val feedDetailUiState: LiveData<FeedDetailUiState> get() = _feedDetailUiState

    fun updateFeedDetail(feedId: Long) {
        viewModelScope.launch {
            runCatching {
                val feed = async { feedRepository.fetchFeed(feedId) }
                val comments = async { feedRepository.fetchComments(feedId) }

                FeedDetailModel(
                    feed = feed.await().toUi(),
                    comments = comments.await().comments.map { it.toUi() },
                )
            }.onSuccess { feedDetail ->
                _feedDetailUiState.value = Success(feedDetail)
            }.onFailure {
                _feedDetailUiState.value = Error
            }
        }
    }

    fun dispatchComment(feedId: Long, comment: String) {
        viewModelScope.launch {
            runCatching {
                feedRepository.saveComment(feedId, comment)
            }.onSuccess {
                updateComments(feedId)
            }.onFailure {
                _feedDetailUiState.value = Error
            }
        }
    }

    private fun updateComments(feedId: Long) {
        viewModelScope.launch {
            runCatching {
                feedRepository.fetchComments(feedId)
            }.onSuccess { comments ->
                _feedDetailUiState.value = Success(
                    feedDetail = (feedDetailUiState.value as Success).feedDetail.copy(
                        comments = comments.comments.map { it.toUi() },
                    ),
                )
            }.onFailure {
                _feedDetailUiState.value = Error
            }
        }
    }

    fun updateReportedSpoilerComment(commentId: Long) {
        feedDetailUiState.value?.let { feedUiState ->
            viewModelScope.launch {
                _feedDetailUiState.value = Loading
                runCatching {
                    feedRepository.saveSpoilerComment(
                        (feedUiState as Success).feedDetail.feed.id,
                        commentId,
                    )
                }.onSuccess {
                    _feedDetailUiState.value = feedUiState as Success
                }.onFailure {
                    _feedDetailUiState.value = Error
                }
            }
        }
    }

    fun updateReportedImpertinenceComment(commentId: Long) {
        feedDetailUiState.value?.let { feedUiState ->
            viewModelScope.launch {
                _feedDetailUiState.value = Loading
                runCatching {
                    feedRepository.saveImpertinenceComment(
                        (feedUiState as Success).feedDetail.feed.id,
                        commentId,
                    )
                }.onSuccess {
                    _feedDetailUiState.value = feedUiState as Success
                }.onFailure {
                    _feedDetailUiState.value = Error
                }
            }
        }
    }

    fun updateRemovedComment(commentId: Long) {
        feedDetailUiState.value?.let { feedUiState ->
            viewModelScope.launch {
                _feedDetailUiState.value = Loading
                runCatching {
                    feedRepository.deleteComment(
                        (feedUiState as Success).feedDetail.feed.id,
                        commentId,
                    )
                }.onSuccess {
                    _feedDetailUiState.value = Success(
                        feedDetail = (feedUiState as Success).feedDetail.copy(
                            comments = feedUiState.feedDetail.comments.filter {
                                it.commentId != commentId
                            },
                        ),
                    )
                }.onFailure {
                    _feedDetailUiState.value = Error
                }
            }
        }
    }
}
