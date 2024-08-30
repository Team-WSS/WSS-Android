package com.teamwss.websoso.ui.feedDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private var feedId: Long = -1
    private val _feedDetailUiState: MutableLiveData<FeedDetailUiState> =
        MutableLiveData(FeedDetailUiState())
    val feedDetailUiState: LiveData<FeedDetailUiState> get() = _feedDetailUiState
    var commentId: Long = -1
        private set

    fun updateCommentId(commentId: Long) {
        this.commentId = commentId
    }

    fun updateFeedDetail(feedId: Long) {
        this.feedId = feedId
        feedDetailUiState.value?.let { feedDetailUiState ->
            viewModelScope.launch {
                _feedDetailUiState.value = feedDetailUiState.copy(loading = true)
                runCatching {
                    val feed = async { feedRepository.fetchFeed(feedId) }
                    val comments = async { feedRepository.fetchComments(feedId) }

                    FeedDetailUiState(
                        feed = feed.await().toUi(),
                        comments = comments.await().comments.map { it.toUi() },
                    )
                }.onSuccess { feedDetail ->
                    _feedDetailUiState.value = feedDetail.copy(loading = false)
                }.onFailure {
                    _feedDetailUiState.value = feedDetailUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

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
                        error = true,
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
                        error = true,
                    )
                }
            }
        }
    }

    fun updateRemovedFeed() {
        feedDetailUiState.value?.let { feedDetailUiState ->
            viewModelScope.launch {
                _feedDetailUiState.value = feedDetailUiState.copy(loading = true)
                runCatching {
                    feedRepository.saveRemovedFeed(feedId)
                }.onSuccess {
                    _feedDetailUiState.value = feedDetailUiState.copy(
                        loading = false,
                    )
                }.onFailure {
                    _feedDetailUiState.value = feedDetailUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateLike(isLiked: Boolean, updatedLikeCount: Int) {
        feedDetailUiState.value?.let { feedDetailUiState ->
            val feed = feedDetailUiState.feed ?: throw IllegalArgumentException()
            if (feed.isLiked == isLiked) return

            viewModelScope.launch {
                runCatching {
                    feedRepository.saveLike(feed.isLiked, feedId)
                }.onSuccess {
                    _feedDetailUiState.value = feedDetailUiState.copy(
                        feed = feedDetailUiState.feed.copy(
                            isLiked = isLiked,
                            likeCount = updatedLikeCount,
                        ),
                    )
                }.onFailure {
                    _feedDetailUiState.value = feedDetailUiState.copy(error = true)
                }
            }
        }
    }

    fun dispatchComment(comment: String) {
        feedDetailUiState.value?.let { feedDetailUiState ->
            viewModelScope.launch {
                _feedDetailUiState.value = feedDetailUiState.copy(loading = true)
                runCatching {
                    feedRepository.saveComment(feedId, comment)
                }.onSuccess {
                    updateComments(feedId)
                }.onFailure {
                    _feedDetailUiState.value = feedDetailUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun modifyComment(comment: String) {
        feedDetailUiState.value?.let { feedDetailUiState ->
            viewModelScope.launch {
                _feedDetailUiState.value = feedDetailUiState.copy(loading = true)
                runCatching {
                    feedRepository.saveModifiedComment(feedId, commentId, comment)
                }.onSuccess {
                    updateComments(feedId)
                }.onFailure {
                    _feedDetailUiState.value = feedDetailUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

    private fun updateComments(feedId: Long) {
        feedDetailUiState.value?.let { feedDetailUiState ->
            viewModelScope.launch {
                runCatching {
                    feedRepository.fetchComments(feedId)
                }.onSuccess { commentsEntity ->
                    val comments = commentsEntity.comments.map { it.toUi() }

                    _feedDetailUiState.value = feedDetailUiState.copy(
                        loading = false,
                        comments = comments,
                        feed = feedDetailUiState.feed?.copy(commentCount = comments.size),
                    )
                }.onFailure {
                    _feedDetailUiState.value = feedDetailUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateReportedSpoilerComment(commentId: Long) {
        feedDetailUiState.value?.let { feedDetailUiState ->
            viewModelScope.launch {
                _feedDetailUiState.value = feedDetailUiState.copy(loading = true)
                runCatching {
                    feedRepository.saveSpoilerComment(feedId, commentId)
                }.onSuccess {
                    _feedDetailUiState.value = feedDetailUiState.copy(loading = false)
                }.onFailure {
                    _feedDetailUiState.value = feedDetailUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateReportedImpertinenceComment(commentId: Long) {
        feedDetailUiState.value?.let { feedDetailUiState ->
            viewModelScope.launch {
                _feedDetailUiState.value = feedDetailUiState.copy(loading = true)
                runCatching {
                    feedRepository.saveImpertinenceComment(feedId, commentId)
                }.onSuccess {
                    _feedDetailUiState.value = feedDetailUiState.copy(loading = false)
                }.onFailure {
                    _feedDetailUiState.value = feedDetailUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateRemovedComment(commentId: Long) {
        feedDetailUiState.value?.let { feedDetailUiState ->
            viewModelScope.launch {
                _feedDetailUiState.value = feedDetailUiState.copy(loading = true)
                runCatching {
                    feedRepository.deleteComment(feedId, commentId)
                }.onSuccess {
                    val comments = feedDetailUiState.comments.filter { it.commentId != commentId }

                    _feedDetailUiState.value = feedDetailUiState.copy(
                        loading = false,
                        comments = comments,
                        feed = feedDetailUiState.feed?.copy(commentCount = comments.size),
                    )
                }.onFailure {
                    _feedDetailUiState.value = feedDetailUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }
}
