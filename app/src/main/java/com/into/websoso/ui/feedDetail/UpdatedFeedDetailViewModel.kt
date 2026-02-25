package com.into.websoso.ui.feedDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.core.common.ui.model.ResultFrom
import com.into.websoso.data.feed.model.CommentEntity
import com.into.websoso.data.feed.repository.UpdatedFeedRepository
import com.into.websoso.data.model.FeedDetailEntity
import com.into.websoso.data.repository.NotificationRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.feedDetail.model.FeedDetailModel
import com.into.websoso.ui.feedDetail.model.FeedDetailUiState
import com.into.websoso.ui.mapper.toCommentModel
import com.into.websoso.ui.mapper.toFeedDetailModel
import com.into.websoso.ui.mapper.toFeedModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatedFeedDetailViewModel @Inject constructor(
    private val feedRepository: UpdatedFeedRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private var feedId: Long = -1
    var commentId: Long = -1
        private set
    private var feedFlowJob: Job? = null

    private val _feedDetailUiState: MutableLiveData<FeedDetailUiState> =
        MutableLiveData(FeedDetailUiState())
    val feedDetailUiState: LiveData<FeedDetailUiState> get() = _feedDetailUiState

    fun updateCommentId(newCommentId: Long) {
        this.commentId = newCommentId
    }

    /**
     * 피드 상세 화면 진입 시 호출되며, 상세 정보, 프로필, 댓글 데이터를 병렬로 로드하고 스트림 구독을 시작합니다.
     */
    fun updateFeedDetail(
        feedId: Long,
        from: ResultFrom,
    ) {
        this.feedId = feedId
        feedFlowJob?.cancel()

        _feedDetailUiState.value = _feedDetailUiState.value?.copy(
            loading = true,
            previousStack = FeedDetailUiState.PreviousStack(from),
        ) ?: FeedDetailUiState()

        viewModelScope.launch {
            launch { fetchSingleFeed(feedId) }
            launch { fetchMyProfile() }
            launch { fetchComments(feedId) }
            observeFeedStream(feedId)
        }
    }

    /**
     * 리포지토리의 여러 피드 소스를 결합하여 현재 피드의 인터랙션 변경 사항(좋아요, 댓글 수)을 실시간으로 관찰합니다.
     */
    private fun observeFeedStream(targetFeedId: Long) {
        feedFlowJob = viewModelScope.launch {
            combine(
                feedRepository.sosoAllFeeds,
                feedRepository.sosoRecommendedFeeds,
                feedRepository.myFeeds,
            ) { sosoAllFeeds, sosoRecommendedFeeds, myFeeds ->
                sosoAllFeeds.find { it.id == targetFeedId }
                    ?: sosoRecommendedFeeds.find { it.id == targetFeedId }
                    ?: myFeeds.find { it.id == targetFeedId }
            }.distinctUntilChanged()
                .collect { feedEntity ->
                    if (feedEntity != null) {
                        val currentUiState = _feedDetailUiState.value ?: FeedDetailUiState()
                        _feedDetailUiState.value = currentUiState.copy(
                            loading = false,
                            isRefreshed = true,
                            feedDetail = currentUiState.feedDetail.copy(
                                feed = feedEntity.toFeedModel(),
                                novel = currentUiState.feedDetail.novel,
                            ),
                        )
                    }
                }
        }
    }

    /**
     * 서버에서 단건 조회를 수행하여 소설 줄거리, 작가 정보 등 상세 데이터를 로드합니다.
     */
    private suspend fun fetchSingleFeed(targetFeedId: Long) {
        runCatching {
            feedRepository.fetchFeed(targetFeedId)
        }.onSuccess { result ->
            val currentUiState = _feedDetailUiState.value ?: return@onSuccess
            _feedDetailUiState.value = currentUiState.copy(
                loading = false,
                isRefreshed = true,
                feedDetail = currentUiState.feedDetail.copy(
                    feed = result.toFeedDetailModel(),
                    novel = result.novel?.let { novelEntity ->
                        FeedDetailEntity.NovelEntity(
                            id = novelEntity.id,
                            title = novelEntity.title,
                            rating = novelEntity.rating,
                            ratingCount = novelEntity.ratingCount,
                            thumbnail = novelEntity.thumbnail,
                            genre = novelEntity.genre,
                            author = novelEntity.author,
                            description = novelEntity.description,
                            feedWriterNovelRating = novelEntity.feedWriterNovelRating,
                        )
                    },
                ),
            )
        }.onFailure {
            val currentUiState = _feedDetailUiState.value ?: return@onFailure
            if (currentUiState.feedDetail.feed == null) {
                _feedDetailUiState.value = currentUiState.copy(loading = false, error = true)
            }
        }
    }

    /**
     * 좋아요 상태를 로컬 캐시에서 즉시 토글합니다.
     */
    fun updateLike() {
        if (feedId == -1L) return
        feedRepository.toggleLikeLocal(feedId)
    }

    /**
     * 화면 이탈 시 로컬의 변경 사항을 서버와 동기화합니다.
     */
    override fun onCleared() {
        super.onCleared()
        feedRepository.syncDirtyFeeds()
    }

    /**
     * 내 프로필 정보를 조회하여 UI 상태를 갱신합니다.
     */
    private suspend fun fetchMyProfile() {
        runCatching { userRepository.fetchMyProfile() }
            .onSuccess { profile ->
                val currentUiState = _feedDetailUiState.value ?: return@onSuccess
                _feedDetailUiState.value = currentUiState.copy(
                    feedDetail = currentUiState.feedDetail.copy(
                        user = FeedDetailModel.UserModel(avatarImage = profile.avatarImage),
                    ),
                )
            }
    }

    /**
     * 피드의 댓글 목록을 조회하여 UI 상태를 갱신합니다.
     */
    private suspend fun fetchComments(targetFeedId: Long) {
        runCatching { feedRepository.fetchComments(targetFeedId) }
            .onSuccess { entity ->
                val currentUiState = _feedDetailUiState.value ?: return@onSuccess
                val uiComments = entity.comments.map(CommentEntity::toCommentModel)
                _feedDetailUiState.value = currentUiState.copy(
                    feedDetail = currentUiState.feedDetail.copy(
                        comments = uiComments,
                        feed = currentUiState.feedDetail.feed?.copy(commentCount = uiComments.size),
                    ),
                )
            }
    }

    /**
     * 새 댓글을 작성하고 목록을 갱신합니다.
     */
    fun dispatchComment(comment: String) {
        viewModelScope.launch {
            runCatching { feedRepository.saveComment(feedId, comment) }
                .onSuccess { fetchComments(feedId) }
                .onFailure {
                    _feedDetailUiState.value =
                        _feedDetailUiState.value?.copy(isServerError = true)
                }
        }
    }

    /**
     * 기존 댓글을 수정하고 목록을 갱신합니다.
     */
    fun modifyComment(comment: String) {
        viewModelScope.launch {
            runCatching { feedRepository.saveModifiedComment(feedId, commentId, comment) }
                .onSuccess { fetchComments(feedId) }
                .onFailure {
                    _feedDetailUiState.value =
                        _feedDetailUiState.value?.copy(isServerError = true)
                }
        }
    }

    /**
     * 댓글을 삭제하고 목록을 갱신합니다.
     */
    fun updateRemovedComment(targetCommentId: Long) {
        viewModelScope.launch {
            _feedDetailUiState.value = _feedDetailUiState.value?.copy(loading = true)
            runCatching { feedRepository.deleteComment(feedId, targetCommentId) }
                .onSuccess {
                    val currentUiState = _feedDetailUiState.value ?: return@onSuccess
                    val newComments =
                        currentUiState.feedDetail.comments.filter { it.commentId != targetCommentId }

                    _feedDetailUiState.value = currentUiState.copy(
                        loading = false,
                        feedDetail = currentUiState.feedDetail.copy(
                            comments = newComments,
                            feed = currentUiState.feedDetail.feed?.copy(commentCount = newComments.size),
                        ),
                    )
                }
                .onFailure {
                    _feedDetailUiState.value =
                        _feedDetailUiState.value?.copy(loading = false, isServerError = true)
                }
        }
    }

    /**
     * 피드를 삭제하고 로컬 캐시 및 UI에 반영합니다.
     */
    fun updateRemovedFeed() {
        viewModelScope.launch {
            _feedDetailUiState.value = _feedDetailUiState.value?.copy(loading = true)
            runCatching { feedRepository.saveRemovedFeed(feedId) }
                .onSuccess {
                    _feedDetailUiState.value = _feedDetailUiState.value?.copy(loading = false)
                }
                .onFailure {
                    _feedDetailUiState.value =
                        _feedDetailUiState.value?.copy(loading = false, error = true)
                }
        }
    }

    /**
     * 피드를 스포일러로 신고합니다.
     */
    fun updateReportedSpoilerFeed() {
        viewModelScope.launch {
            runCatching { feedRepository.saveSpoilerFeed(feedId) }
        }
    }

    /**
     * 피드를 부적절한 게시물로 신고합니다.
     */
    fun updateReportedImpertinenceFeed() {
        viewModelScope.launch {
            runCatching { feedRepository.saveImpertinenceFeed(feedId) }
        }
    }

    /**
     * 댓글을 스포일러로 신고합니다.
     */
    fun updateReportedSpoilerComment(targetCommentId: Long) {
        viewModelScope.launch {
            runCatching { feedRepository.saveSpoilerComment(feedId, targetCommentId) }
        }
    }

    /**
     * 댓글을 부적절한 내용으로 신고합니다.
     */
    fun updateReportedImpertinenceComment(targetCommentId: Long) {
        viewModelScope.launch {
            runCatching { feedRepository.saveImpertinenceComment(feedId, targetCommentId) }
        }
    }

    /**
     * 알림 읽음 처리를 수행합니다.
     */
    fun updateNotificationRead(notificationId: Long) {
        if (notificationId == DEFAULT_NOTIFICATION_ID) return
        viewModelScope.launch {
            runCatching { notificationRepository.fetchNotificationRead(notificationId) }
        }
    }

    companion object {
        const val DEFAULT_NOTIFICATION_ID: Long = -1
    }
}
