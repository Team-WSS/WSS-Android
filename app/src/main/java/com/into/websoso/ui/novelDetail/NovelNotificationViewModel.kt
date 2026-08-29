package com.into.websoso.ui.novelDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.domain.model.NovelNotificationSetting
import com.into.websoso.domain.usecase.GetNovelNotificationSettingUseCase
import com.into.websoso.domain.usecase.UpdateNovelNotificationSettingUseCase
import com.into.websoso.ui.novelDetail.model.NovelNotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelNotificationViewModel
    @Inject
    constructor(
        private val getNovelNotificationSettingUseCase: GetNovelNotificationSettingUseCase,
        private val updateNovelNotificationSettingUseCase: UpdateNovelNotificationSettingUseCase,
    ) : ViewModel() {
        private val _novelNotificationUiState: MutableStateFlow<NovelNotificationUiState> =
            MutableStateFlow(NovelNotificationUiState())
        val novelNotificationUiState: StateFlow<NovelNotificationUiState> get() = _novelNotificationUiState

        private var syncedNovelNotificationSetting: NovelNotificationSetting = NovelNotificationSetting()
        private var saveJob: Job? = null

        // 토글 직후 바텀시트를 닫으면 viewModelScope가 취소돼 저장이 유실되므로,
        // 저장만은 ViewModel 생명주기와 분리된 스코프에서 실행한다 (onCleared에서 취소하지 않는다)
        private val saveScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

        fun updateNovelNotificationSetting(novelId: Long) {
            _novelNotificationUiState.value = novelNotificationUiState.value.copy(
                isLoading = true,
                isError = false,
            )

            viewModelScope.launch {
                getNovelNotificationSettingUseCase(novelId)
                    .onSuccess { novelNotificationSetting ->
                        syncedNovelNotificationSetting = novelNotificationSetting
                        _novelNotificationUiState.value = novelNotificationUiState.value.copy(
                            isLoading = false,
                            isError = false,
                            isCompletionNotificationEnabled = novelNotificationSetting.isCompletionNotificationEnabled,
                            isHiatusReturnNotificationEnabled = novelNotificationSetting.isHiatusReturnNotificationEnabled,
                        )
                    }.onFailure {
                        _novelNotificationUiState.value = novelNotificationUiState.value.copy(
                            isLoading = false,
                            isError = true,
                        )
                    }
            }
        }

        fun updateCompletionNotificationEnabled(
            novelId: Long,
            isEnabled: Boolean,
        ) {
            if (novelNotificationUiState.value.isEditable.not()) return

            _novelNotificationUiState.value = novelNotificationUiState.value.copy(
                isCompletionNotificationEnabled = isEnabled,
            )

            saveNovelNotificationSetting(novelId)
        }

        fun updateHiatusReturnNotificationEnabled(
            novelId: Long,
            isEnabled: Boolean,
        ) {
            if (novelNotificationUiState.value.isEditable.not()) return

            _novelNotificationUiState.value = novelNotificationUiState.value.copy(
                isHiatusReturnNotificationEnabled = isEnabled,
            )

            saveNovelNotificationSetting(novelId)
        }

        private fun saveNovelNotificationSetting(novelId: Long) {
            saveJob?.cancel()
            saveJob = saveScope.launch {
                delay(SAVE_DEBOUNCE_MILLIS)

                val requestedNovelNotificationSetting = NovelNotificationSetting(
                    isCompletionNotificationEnabled = novelNotificationUiState.value.isCompletionNotificationEnabled,
                    isHiatusReturnNotificationEnabled = novelNotificationUiState.value.isHiatusReturnNotificationEnabled,
                )

                updateNovelNotificationSettingUseCase(
                    novelId = novelId,
                    novelNotificationSetting = requestedNovelNotificationSetting,
                ).onSuccess {
                    syncedNovelNotificationSetting = requestedNovelNotificationSetting
                }.onFailure {
                    _novelNotificationUiState.value = novelNotificationUiState.value.copy(
                        isCompletionNotificationEnabled = syncedNovelNotificationSetting.isCompletionNotificationEnabled,
                        isHiatusReturnNotificationEnabled = syncedNovelNotificationSetting.isHiatusReturnNotificationEnabled,
                    )
                }
            }
        }

        companion object {
            private const val SAVE_DEBOUNCE_MILLIS = 300L
        }
    }
