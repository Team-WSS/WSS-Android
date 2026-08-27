package com.into.websoso.ui.novelNotification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.domain.model.NovelNotificationSubscriptions
import com.into.websoso.domain.model.NovelNotificationType
import com.into.websoso.domain.usecase.GetNovelNotificationSubscriptionsUseCase
import com.into.websoso.ui.mapper.toUi
import com.into.websoso.ui.novelNotification.NovelNotificationListActivity.Companion.NOVEL_NOTIFICATION_TYPE
import com.into.websoso.ui.novelNotification.model.NovelNotificationListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelNotificationListViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val getNovelNotificationSubscriptionsUseCase: GetNovelNotificationSubscriptionsUseCase,
    ) : ViewModel() {
        private val notificationType: NovelNotificationType = NovelNotificationType.from(
            savedStateHandle.get<String>(NOVEL_NOTIFICATION_TYPE).orEmpty(),
        )

        private val _novelNotificationListUiState: MutableStateFlow<NovelNotificationListUiState> =
            MutableStateFlow(NovelNotificationListUiState(notificationType = notificationType))
        val novelNotificationListUiState: StateFlow<NovelNotificationListUiState> get() = _novelNotificationListUiState

        init {
            updateSubscriptions()
        }

        fun updateSubscriptions() {
            val currentUiState = novelNotificationListUiState.value
            if (currentUiState.isLoadable.not() || currentUiState.isLoading) return

            _novelNotificationListUiState.value = currentUiState.copy(
                isLoading = true,
                isError = false,
            )

            viewModelScope.launch {
                getNovelNotificationSubscriptionsUseCase(
                    notificationType = notificationType,
                    lastSubscriptionId = currentUiState.lastSubscriptionId,
                ).onSuccess { novelNotificationSubscriptions ->
                    handleSuccessState(novelNotificationSubscriptions)
                }.onFailure {
                    handleFailureState()
                }
            }
        }

        private fun handleSuccessState(novelNotificationSubscriptions: NovelNotificationSubscriptions) {
            val currentUiState = novelNotificationListUiState.value
            _novelNotificationListUiState.value = currentUiState.copy(
                isLoadable = novelNotificationSubscriptions.isLoadable,
                isLoading = false,
                isError = false,
                isInitialLoaded = true,
                lastSubscriptionId = novelNotificationSubscriptions.nextSubscriptionId,
                subscriptions = currentUiState.subscriptions +
                    novelNotificationSubscriptions.subscriptions.map { it.toUi() },
            )
        }

        private fun handleFailureState() {
            _novelNotificationListUiState.value = novelNotificationListUiState.value.copy(
                isLoading = false,
                isError = true,
                isInitialLoaded = true,
            )
        }
    }
