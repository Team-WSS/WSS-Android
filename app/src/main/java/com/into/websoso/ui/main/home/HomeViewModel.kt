package com.into.websoso.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.model.PopularFeedsEntity
import com.into.websoso.data.model.PopularNovelsEntity
import com.into.websoso.data.model.RecommendedNovelsByUserTasteEntity
import com.into.websoso.data.model.TermsAgreementEntity
import com.into.websoso.data.model.UserInterestFeedMessage
import com.into.websoso.data.model.UserInterestFeedMessage.NO_INTEREST_NOVELS
import com.into.websoso.data.model.UserInterestFeedsEntity
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.data.repository.NotificationRepository
import com.into.websoso.data.repository.NovelRepository
import com.into.websoso.data.repository.PushMessageRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.main.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    ) : ViewModel() {
        private val _uiState: MutableLiveData<HomeUiState> = MutableLiveData(HomeUiState())
        val uiState: LiveData<HomeUiState> get() = _uiState

        private val _isNotificationPermissionFirstLaunched: MutableLiveData<Boolean> = MutableLiveData()
        val isNotificationPermissionFirstLaunched: LiveData<Boolean> get() = _isNotificationPermissionFirstLaunched

        private val termsAgreementState = MutableStateFlow<TermsAgreementEntity?>(null)

        private val _showTermsAgreementDialog = MutableStateFlow(false)
        val showTermsAgreementDialog: StateFlow<Boolean> = _showTermsAgreementDialog.asStateFlow()

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
            viewModelScope.launch {
                runCatching {
                    val results = listOf(
                        async { runCatching { novelRepository.fetchPopularNovels() } },
                        async { runCatching { feedRepository.fetchPopularFeeds() } },
                        async { runCatching { feedRepository.fetchUserInterestFeeds() } },
                        async { runCatching { novelRepository.fetchRecommendedNovelsByUserTaste() } },
                    ).awaitAll()

                    // 실패가 하나라도 있다면 상위 onFailure로 예외 전파
                    val failures = results.filter { it.isFailure }
                    if (failures.isNotEmpty()) {
                        throw failures.first().exceptionOrNull()
                            ?: IllegalStateException("Unknown error")
                    }

                    val popularNovels = results[0].getOrNull() as? PopularNovelsEntity
                        ?: PopularNovelsEntity(emptyList())
                    val popularFeeds = results[1].getOrNull() as? PopularFeedsEntity
                        ?: PopularFeedsEntity(emptyList())
                    val userInterestFeeds = results[2].getOrNull() as? UserInterestFeedsEntity
                        ?: UserInterestFeedsEntity(emptyList(), "")
                    val recommendedNovels =
                        results[3].getOrNull() as? RecommendedNovelsByUserTasteEntity
                            ?: RecommendedNovelsByUserTasteEntity(emptyList())

                    _uiState.value = uiState.value?.copy(
                        loading = false,
                        error = false,
                        popularNovels = popularNovels.popularNovels,
                        popularFeeds = popularFeeds.popularFeeds.chunked(3),
                        isInterestNovel = isUserInterestedInNovels(userInterestFeeds.message),
                        userInterestFeeds = userInterestFeeds.userInterestFeeds,
                        recommendedNovelsByUserTaste = recommendedNovels.tasteNovels,
                    )
                }.onFailure {
                    _uiState.value = uiState.value?.copy(
                        loading = false,
                        error = true,
                    )
                }
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
            viewModelScope.launch {
                runCatching {
                    listOf(
                        async { novelRepository.fetchPopularNovels() },
                        async { feedRepository.fetchPopularFeeds() },
                    ).awaitAll()
                }.onSuccess { responses ->
                    val popularNovels = responses[0] as PopularNovelsEntity
                    val popularFeeds = responses[1] as PopularFeedsEntity

                    _uiState.value = uiState.value?.copy(
                        loading = false,
                        popularNovels = popularNovels.popularNovels,
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

        fun updateFeed() {
            viewModelScope.launch {
                runCatching {
                    listOf(
                        async { feedRepository.fetchPopularFeeds() },
                        async { feedRepository.fetchUserInterestFeeds() },
                    ).awaitAll()
                }.onSuccess { responses ->
                    val popularFeeds = responses[0] as PopularFeedsEntity
                    val userInterestFeeds = responses[1] as UserInterestFeedsEntity

                    _uiState.value = uiState.value?.copy(
                        popularFeeds = popularFeeds.popularFeeds.chunked(3),
                        isInterestNovel = isUserInterestedInNovels(userInterestFeeds.message),
                        userInterestFeeds = userInterestFeeds.userInterestFeeds,
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

        private fun isUserInterestedInNovels(userInterestFeedMessage: String): Boolean =
            when (UserInterestFeedMessage.fromMessage(userInterestFeedMessage)) {
                NO_INTEREST_NOVELS -> false
                else -> true
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
                        _showTermsAgreementDialog.value = !(terms.serviceAgreed && terms.privacyAgreed)
                    }
            }
        }

        fun updateFCMToken(token: String) {
            viewModelScope.launch {
                runCatching {
                    pushMessageRepository.updateUserFCMToken(token)
                }
            }
        }
    }
