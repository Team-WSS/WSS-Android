package com.into.websoso.ui.novelNotification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.domain.model.NovelNotificationSubscriptions
import com.into.websoso.domain.model.NovelNotificationType
import com.into.websoso.domain.usecase.DeleteNovelNotificationSubscriptionsUseCase
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
        private val deleteNovelNotificationSubscriptionsUseCase: DeleteNovelNotificationSubscriptionsUseCase,
    ) : ViewModel() {
        private val notificationType: NovelNotificationType = NovelNotificationType.from(
            savedStateHandle.get<String>(NOVEL_NOTIFICATION_TYPE).orEmpty(),
        )

        private val _novelNotificationListUiState: MutableStateFlow<NovelNotificationListUiState> =
            MutableStateFlow(NovelNotificationListUiState(notificationType = notificationType))
        val novelNotificationListUiState: StateFlow<NovelNotificationListUiState> get() = _novelNotificationListUiState

        // 삭제로 목록이 비었는데 페이지 요청이 진행 중이면 isLoading 가드에 막혀 후속 조회가 사라지므로,
        // 진행 중인 요청이 끝난 뒤 이어서 조회하도록 예약해 둔다
        private var isRefetchPending = false

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

            consumeRefetchPending()
        }

        private fun consumeRefetchPending() {
            if (isRefetchPending.not()) return
            isRefetchPending = false

            if (novelNotificationListUiState.value.subscriptions.isEmpty()) updateSubscriptions()
        }

        private fun handleFailureState() {
            // 실패했을 때는 자동으로 다시 부르지 않고 오류 화면의 재시도 버튼에 맡긴다
            isRefetchPending = false

            _novelNotificationListUiState.value = novelNotificationListUiState.value.copy(
                isLoading = false,
                isError = true,
                isInitialLoaded = true,
            )
        }

        fun updateEditing(isEditing: Boolean) {
            _novelNotificationListUiState.value = novelNotificationListUiState.value.copy(
                isEditing = isEditing,
                selectedNovelIds = emptySet(),
            )
        }

        fun updateSelectedNovel(novelId: Long) {
            val currentUiState = novelNotificationListUiState.value
            val selectedNovelIds = when (novelId in currentUiState.selectedNovelIds) {
                true -> currentUiState.selectedNovelIds - novelId
                false -> currentUiState.selectedNovelIds + novelId
            }

            _novelNotificationListUiState.value = currentUiState.copy(selectedNovelIds = selectedNovelIds)
        }

        fun updateDeleteDialogVisibility(isVisible: Boolean) {
            _novelNotificationListUiState.value = novelNotificationListUiState.value.copy(
                isDeleteDialogVisible = isVisible,
            )
        }

        fun deleteSelectedSubscriptions() {
            val selectedNovelIds = novelNotificationListUiState.value.selectedNovelIds
            if (selectedNovelIds.isEmpty()) return

            viewModelScope.launch {
                deleteNovelNotificationSubscriptionsUseCase(
                    notificationType = notificationType,
                    novelIds = selectedNovelIds.toList(),
                ).onSuccess {
                    handleDeleteSuccessState(selectedNovelIds)
                }.onFailure {
                    updateDeleteDialogVisibility(false)
                }
            }
        }

        private fun handleDeleteSuccessState(deletedNovelIds: Set<Long>) {
            val currentUiState = novelNotificationListUiState.value
            val remainedSubscriptions = currentUiState.subscriptions.filterNot { it.novelId in deletedNovelIds }

            _novelNotificationListUiState.value = currentUiState.copy(
                isEditing = false,
                isDeleteDialogVisible = false,
                selectedNovelIds = emptySet(),
                subscriptions = remainedSubscriptions,
            )

            // 로드된 항목을 모두 삭제해도 다음 페이지가 남아 있으면 빈 화면 대신 이어서 불러온다
            if (remainedSubscriptions.isEmpty() && currentUiState.isLoadable) {
                when (currentUiState.isLoading) {
                    true -> isRefetchPending = true
                    false -> updateSubscriptions()
                }
            }
        }
    }
