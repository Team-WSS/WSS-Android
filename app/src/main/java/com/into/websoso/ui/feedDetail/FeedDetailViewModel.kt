package com.into.websoso.ui.feedDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.core.common.ui.model.ResultFrom
import com.into.websoso.data.model.CommentsEntity
import com.into.websoso.data.model.FeedEntity
import com.into.websoso.data.model.MyProfileEntity
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.data.repository.NotificationRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.feedDetail.model.FeedDetailModel
import com.into.websoso.ui.feedDetail.model.FeedDetailUiState
import com.into.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel
    @Inject
    constructor(
        private val feedRepository: FeedRepository,
        private val userRepository: UserRepository,
        private val notificationRepository: NotificationRepository,
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

        fun updateFeedDetail(
            feedId: Long,
            from: ResultFrom,
            isLiked: Boolean = false,
        ) {
            this.feedId = feedId
            feedDetailUiState.value?.let { feedDetailUiState ->
                viewModelScope.launch {
                    delay(300)
                    _feedDetailUiState.value = feedDetailUiState.copy(loading = true)

                    runCatching {
                        coroutineScope {
                            awaitAll(
                                async { userRepository.fetchMyProfile() },
                                async { feedRepository.fetchFeed(feedId) },
                                async { feedRepository.fetchComments(feedId) },
                            )
                        }
                    }.onSuccess { result ->
                        val myProfile = result[0] as MyProfileEntity
                        val feed = (result[1] as FeedEntity)
                        val comments = result[2] as CommentsEntity

                        val uiFeed = feed.toUi()
                        val updatedFeed = if (feed.isLiked == isLiked) {
                            uiFeed
                        } else if (!isLiked && feed.isLiked) {
                            uiFeed.copy(
                                isLiked = false,
                                likeCount = feed.likeCount - 1,
                            )
                        } else {
                            uiFeed.copy(
                                isLiked = true,
                                likeCount = feed.likeCount + 1,
                            )
                        }

                        _feedDetailUiState.value = feedDetailUiState.copy(
                            loading = false,
                            isRefreshed = true,
                            feedDetail = FeedDetailModel(
                                feed = updatedFeed,
                                comments = comments.comments.map { it.toUi() },
                                user = FeedDetailModel.UserModel(
                                    avatarImage = myProfile.avatarImage,
                                ),
                            ),
                        )
                    }.onFailure {
                        _feedDetailUiState.value = feedDetailUiState.copy(
                            loading = false,
                            error = true,
                            previousStack = FeedDetailUiState.PreviousStack(from),
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

        fun updateLike(
            isLiked: Boolean,
            updatedLikeCount: Int,
        ) {
            feedDetailUiState.value?.let { feedDetailUiState ->
                val feed = feedDetailUiState.feedDetail.feed ?: throw IllegalArgumentException()
                if (feed.isLiked == isLiked) return

                viewModelScope.launch {
                    runCatching {
                        feedRepository.saveLike(feed.isLiked, feedId)
                    }.onSuccess {
                        _feedDetailUiState.value = feedDetailUiState.copy(
                            feedDetail = feedDetailUiState.feedDetail.copy(
                                feed = feedDetailUiState.feedDetail.feed.copy(
                                    isLiked = isLiked,
                                    likeCount = updatedLikeCount,
                                ),
                            ),
                        )
                    }.onFailure {
                        _feedDetailUiState.value = feedDetailUiState.copy(
                            isServerError = true,
                        )
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
                            isServerError = true,
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
                            isServerError = true,
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
                            isRefreshed = false,
                            feedDetail = feedDetailUiState.feedDetail.copy(
                                feed = feedDetailUiState.feedDetail.feed?.copy(commentCount = comments.size),
                                comments = comments,
                            ),
                        )
                    }.onFailure {
                        _feedDetailUiState.value = feedDetailUiState.copy(
                            loading = false,
                            isServerError = true,
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
                            isReported = true,
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
                            isReported = true,
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
                        val comments =
                            feedDetailUiState.feedDetail.comments.filter { it.commentId != commentId }

                        _feedDetailUiState.value = feedDetailUiState.copy(
                            loading = false,
                            feedDetail = feedDetailUiState.feedDetail.copy(
                                feed = feedDetailUiState.feedDetail.feed?.copy(commentCount = comments.size),
                                comments = comments,
                            ),
                        )
                    }.onFailure {
                        _feedDetailUiState.value = feedDetailUiState.copy(
                            loading = false,
                            isServerError = true,
                        )
                    }
                }
            }
        }

        fun updateNotificationRead(notificationId: Long) {
            if (notificationId == DEFAULT_NOTIFICATION_ID) return
            viewModelScope.launch {
                runCatching {
                    notificationRepository.fetchNotificationRead(notificationId)
                }
            }
        }

        companion object {
            const val DEFAULT_NOTIFICATION_ID: Long = -1
        }
    }
