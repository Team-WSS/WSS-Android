package com.into.websoso.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.model.PopularFeedsEntity
import com.into.websoso.data.model.PopularNovelsEntity
import com.into.websoso.data.model.RecommendedNovelsByUserTasteEntity
import com.into.websoso.data.model.TermsAgreementEntity
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.data.repository.NotificationRepository
import com.into.websoso.data.repository.NovelRepository
import com.into.websoso.data.repository.PushMessageRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.main.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val novelRepository: NovelRepository,
        private val feedRepository: FeedRepository,
        private val pushMessageRepository: PushMessageRepository,
        private val notificationRepository: NotificationRepository,
        private val userRepository: UserRepository,
        private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _uiState: MutableLiveData<HomeUiState> = MutableLiveData(HomeUiState())
        val uiState: LiveData<HomeUiState> get() = _uiState

        private val _isNotificationPermissionFirstLaunched: MutableLiveData<Boolean> = MutableLiveData()
        val isNotificationPermissionFirstLaunched: LiveData<Boolean> get() = _isNotificationPermissionFirstLaunched

        private val termsAgreementState = MutableStateFlow<TermsAgreementEntity?>(null)

        private val _showTermsAgreementDialog = MutableStateFlow(false)
        val showTermsAgreementDialog: StateFlow<Boolean> = _showTermsAgreementDialog.asStateFlow()

        private var isTermsAgreementChecked: Boolean
            get() = savedStateHandle["isTermsAgreementChecked"] ?: false
            set(value) {
                savedStateHandle["isTermsAgreementChecked"] = value
            }

        init {
            updateHomeData(true)
            updateNotificationUnread()
            checkTermsAgreement()
        }

        private fun updateHomeData(isLogin: Boolean) {
            viewModelScope.launch {
                if (isLogin) {
                    fetchUserHomeData()
                    checkIsNotificationPermissionFirstLaunched()
                } else {
                    fetchGuestData()
                }
            }
        }

        private suspend fun fetchUserHomeData() {
            coroutineScope {
                val popularNovelsDeferred =
                    async { runCatching { novelRepository.fetchPopularNovels() } }
                val popularFeedsDeferred =
                    async { runCatching { feedRepository.fetchPopularFeeds() } }
                val recommendedNovelsDeferred =
                    async { runCatching { novelRepository.fetchRecommendedNovelsByUserTaste() } }

                val popularNovelsResult = popularNovelsDeferred.await()
                val popularFeedsResult = popularFeedsDeferred.await()
                val recommendedNovelsResult = recommendedNovelsDeferred.await()

                val failure = popularNovelsResult.exceptionOrNull()
                    ?: popularFeedsResult.exceptionOrNull()
                    ?: recommendedNovelsResult.exceptionOrNull()
                if (failure != null) {
                    handleFailureState()
                    return@coroutineScope
                }

                val popularNovels = popularNovelsResult.getOrThrow()
                val popularFeeds = popularFeedsResult.getOrThrow()
                val recommendedNovels = recommendedNovelsResult.getOrThrow()

                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = false,
                    popularNovels = popularNovels.popularNovels,
                    popularFeeds = popularFeeds.popularFeeds.toHomePopularFeedPages(),
                    recommendedNovelsByUserTaste = recommendedNovels.tasteNovels,
                )
            }
        }

        private fun checkIsNotificationPermissionFirstLaunched() {
            viewModelScope.launch {
                runCatching {
                    pushMessageRepository.fetchNotificationPermissionFirstLaunched()
                }.onSuccess { isFirstLaunched ->
                    _isNotificationPermissionFirstLaunched.value = isFirstLaunched
                }
            }
        }

        fun updateIsNotificationPermissionFirstLaunched(isFirstLaunched: Boolean) {
            viewModelScope.launch {
                runCatching {
                    pushMessageRepository.saveNotificationPermissionFirstLaunched(isFirstLaunched)
                }.onSuccess {
                    _isNotificationPermissionFirstLaunched.value = isFirstLaunched
                }
            }
        }

        private suspend fun fetchGuestData() {
            coroutineScope {
                val popularNovelsDeferred =
                    async { runCatching { novelRepository.fetchPopularNovels() } }
                val popularFeedsDeferred =
                    async { runCatching { feedRepository.fetchPopularFeeds() } }

                val popularNovelsResult = popularNovelsDeferred.await()
                val popularFeedsResult = popularFeedsDeferred.await()

                val failure = popularNovelsResult.exceptionOrNull()
                    ?: popularFeedsResult.exceptionOrNull()
                if (failure != null) {
                    handleFailureState()
                    return@coroutineScope
                }

                val popularNovels = popularNovelsResult.getOrThrow()
                val popularFeeds = popularFeedsResult.getOrThrow()

                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = false,
                    popularNovels = popularNovels.popularNovels,
                    popularFeeds = popularFeeds.popularFeeds.toHomePopularFeedPages(),
                )
            }
        }

        private fun handleFailureState() {
            _uiState.value = uiState.value?.copy(
                loading = false,
                error = true,
            )
        }

        fun updateFeed() {
            viewModelScope.launch {
                runCatching {
                    feedRepository.fetchPopularFeeds()
                }.onSuccess { popularFeeds ->
                    _uiState.value = uiState.value?.copy(
                        error = false,
                        popularFeeds = popularFeeds.popularFeeds.toHomePopularFeedPages(),
                    )
                }.onFailure {
                    _uiState.value = uiState.value?.copy(error = true)
                }
            }
        }

        fun updateNovel() {
            viewModelScope.launch {
                runCatching {
                    listOf(
                        async { novelRepository.fetchPopularNovels() },
                        async { novelRepository.fetchRecommendedNovelsByUserTaste() },
                    ).awaitAll()
                }.onSuccess { responses ->
                    val popularNovels = responses[0] as PopularNovelsEntity
                    val recommendedNovels = responses[1] as RecommendedNovelsByUserTasteEntity

                    _uiState.value = uiState.value?.copy(
                        popularNovels = popularNovels.popularNovels,
                        recommendedNovelsByUserTaste = recommendedNovels.tasteNovels,
                    )
                }.onFailure {
                    _uiState.value = uiState.value?.copy(
                        error = true,
                    )
                }
            }
        }

        fun updateNotificationUnread() {
            viewModelScope.launch {
                runCatching {
                    notificationRepository.fetchNotificationUnread()
                }.onSuccess { isNotificationUnread ->
                    _uiState.value = uiState.value?.copy(
                        isNotificationUnread = isNotificationUnread,
                    )
                }.onFailure {
                    _uiState.value = uiState.value?.copy(
                        error = true,
                    )
                }
            }
        }

        fun saveFCMToken(token: String) {
            viewModelScope.launch {
                runCatching {
                    pushMessageRepository.saveUserFCMToken(token)
                }
            }
        }

        private fun checkTermsAgreement() {
            viewModelScope.launch {
                userRepository.isTermsAgreementChecked.collect { checked ->
                    if (!checked) {
                        updateTermsAgreement()
                    }
                }
            }
        }

        private fun updateTermsAgreement() {
            viewModelScope.launch {
                runCatching { userRepository.fetchTermsAgreements() }
                    .onSuccess { terms ->
                        termsAgreementState.value = terms
                        val isShownDialog = !(terms.serviceAgreed && terms.privacyAgreed)

                        _showTermsAgreementDialog.value = isShownDialog

                        if (!isShownDialog) {
                            isTermsAgreementChecked = true
                        }
                    }
            }
        }

        fun updateTermsAgreementDialogState() {
            _showTermsAgreementDialog.value = false
        }

        fun updateFCMToken(token: String) {
            viewModelScope.launch {
                runCatching {
                    pushMessageRepository.updateUserFCMToken(token)
                }
            }
        }

        private fun List<PopularFeedsEntity.PopularFeedEntity>.toHomePopularFeedPages(): List<List<PopularFeedsEntity.PopularFeedEntity>> =
            take(HOME_POPULAR_FEED_MAX_COUNT).chunked(HOME_POPULAR_FEED_PAGE_SIZE)

        companion object {
            private const val HOME_POPULAR_FEED_MAX_COUNT = 6
            private const val HOME_POPULAR_FEED_PAGE_SIZE = 2
        }
    }
