package com.into.websoso.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.model.PopularFeedEntity
import com.into.websoso.data.model.TermsAgreementEntity
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.data.repository.NotificationRepository
import com.into.websoso.data.repository.NovelRepository
import com.into.websoso.data.repository.PushMessageRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.main.home.model.HomeTasteStatus
import com.into.websoso.ui.main.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import retrofit2.HttpException
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
        homeStartupRequest: HomeStartupRequest,
    ) : ViewModel() {
        // Received data is kept here even while loading; the Fragment waits before rendering it.
        private val _uiState: MutableLiveData<HomeUiState> = MutableLiveData(HomeUiState())
        val uiState: LiveData<HomeUiState> get() = _uiState

        // Foreground notice only; returning to Home must not replay an old refresh failure.
        private val _tasteRefreshFailed = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
        val tasteRefreshFailed: SharedFlow<Unit> = _tasteRefreshFailed.asSharedFlow()

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

        private val sectionLoader = HomeSectionLoader(viewModelScope)
        private val sectionRelease = HomeSectionRelease()
        private var hasSessionFailure = false
        private val startupRequests = homeStartupRequest.take(savedStateHandle.remove<String>(HomeStartupRequest.KEY))

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
            val requests = listOf(loadPopularNovels(initial = true), loadPopularFeeds(initial = true), loadTasteNovels())
            requests.joinAll()
            if (requests.all { !it.isCancelled && it.await() }) recoverGlobalError()
        }

        private fun loadPopularNovels(initial: Boolean = false): Deferred<Boolean> {
            if (!initial) startupRequests?.cancelPopular()
            return sectionLoader.load(
                section = HomeSection.POPULAR,
                request = { (if (initial) startupRequests?.popular() else null) ?: novelRepository.fetchPopularNovels() },
                success = { result ->
                    updateContent(HomeSection.POPULAR) { it.copy(popularNovels = result.popularNovels) }
                },
                failure = { handleFailureState(it) },
            )
        }

        private fun loadPopularFeeds(
            recoverError: Boolean = false,
            initial: Boolean = false,
        ): Deferred<Boolean> {
            if (!initial) startupRequests?.cancelFeeds()
            return sectionLoader.load(
                section = HomeSection.FEEDS,
                request = { (if (initial) startupRequests?.feeds() else null) ?: feedRepository.fetchPopularFeeds() },
                success = { result ->
                    updateContent(HomeSection.FEEDS) { it.copy(popularFeeds = result.toHomePopularFeedPages()) }
                    if (recoverError) recoverGlobalError()
                },
                failure = { handleFailureState(it) },
            )
        }

        override fun onCleared() {
            startupRequests?.cancel()
            super.onCleared()
        }

        private fun loadTasteNovels(preferencesChanged: Boolean = false): Deferred<Boolean> {
            val previousStatus = _uiState.value?.tasteStatus
            val keepVisible = !preferencesChanged &&
                (previousStatus == HomeTasteStatus.CONTENT || previousStatus == HomeTasteStatus.EMPTY)
            if (!keepVisible) {
                updateContent(HomeSection.TASTE) { current ->
                    current.copy(
                        tasteStatus = HomeTasteStatus.LOADING,
                        recommendedNovelsByUserTaste = if (preferencesChanged) emptyList() else current.recommendedNovelsByUserTaste,
                    )
                }
            }
            return sectionLoader.load(
                section = HomeSection.TASTE,
                request = novelRepository::fetchRecommendedNovelsByUserTaste,
                success = { result ->
                    updateContent(HomeSection.TASTE) { current ->
                        current.copy(
                            recommendedNovelsByUserTaste = result.tasteNovels,
                            tasteStatus = if (result.tasteNovels.isEmpty()) HomeTasteStatus.EMPTY else HomeTasteStatus.CONTENT,
                        )
                    }
                },
                failure = { error ->
                    if (isSessionFailure(error)) {
                        handleFailureState(error)
                    } else if (keepVisible && (error !is HttpException || error.code() != 403)) {
                        _tasteRefreshFailed.tryEmit(Unit)
                    } else {
                        updateContent(HomeSection.TASTE) { it.copy(tasteStatus = HomeTasteStatus.ERROR) }
                    }
                },
            )
        }

        private fun updateContent(
            section: HomeSection,
            transform: (HomeUiState) -> HomeUiState,
        ) {
            updateState { current ->
                val updated = transform(current)
                if (sectionRelease.update(section)) updated.copy(loading = false) else updated
            }
        }

        private fun updateState(transform: (HomeUiState) -> HomeUiState) {
            val current = _uiState.value ?: return
            val updated = transform(current)
            if (updated != current) _uiState.value = updated
        }

        // Match the existing recovery events; a content response cannot establish a new session.
        private fun recoverGlobalError() {
            if (hasSessionFailure) return
            updateState { current ->
                if (!current.error) return@updateState current
                sectionRelease.recover(loading = current.loading)
                current.copy(error = false)
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
                    handleFailureState(failure)
                    return@coroutineScope
                }

                val popularNovels = popularNovelsResult.getOrThrow()
                val popularFeeds = popularFeedsResult.getOrThrow()

                updateState { current ->
                    current.copy(
                        loading = false,
                        error = false,
                        popularNovels = popularNovels.popularNovels,
                        popularFeeds = popularFeeds.toHomePopularFeedPages(),
                    )
                }
            }
        }

        private fun handleFailureState(
            error: Throwable,
            preserveLoading: Boolean = false,
        ) {
            hasSessionFailure = hasSessionFailure || isSessionFailure(error)
            updateState { current ->
                current.copy(
                    loading = preserveLoading && current.loading,
                    error = true,
                )
            }
        }

        fun updateFeed() {
            loadPopularFeeds(recoverError = true)
        }

        fun updateNovel(preferencesChanged: Boolean = false) {
            loadPopularNovels()
            loadTasteNovels(preferencesChanged)
        }

        fun retryTaste() {
            val current = _uiState.value ?: return
            if (current.error || current.tasteStatus != HomeTasteStatus.ERROR) return
            loadTasteNovels()
        }

        fun updateNotificationUnread() {
            viewModelScope.launch {
                runCatching {
                    notificationRepository.fetchNotificationUnread()
                }.onSuccess { isNotificationUnread ->
                    updateState { it.copy(isNotificationUnread = isNotificationUnread) }
                }.onFailure { error ->
                    handleFailureState(error, preserveLoading = true)
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

        private fun List<PopularFeedEntity>.toHomePopularFeedPages(): List<List<PopularFeedEntity>> =
            take(HOME_POPULAR_FEED_MAX_COUNT).chunked(HOME_POPULAR_FEED_PAGE_SIZE)

        companion object {
            private const val HOME_POPULAR_FEED_MAX_COUNT = 6
            private const val HOME_POPULAR_FEED_PAGE_SIZE = 2
        }
    }
