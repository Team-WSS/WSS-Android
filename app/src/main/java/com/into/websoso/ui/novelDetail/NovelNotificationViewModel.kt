package com.into.websoso.ui.novelDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.domain.model.NovelNotificationSetting
import com.into.websoso.domain.usecase.GetNovelNotificationSettingUseCase
import com.into.websoso.domain.usecase.UpdateNovelNotificationSettingUseCase
import com.into.websoso.ui.novelDetail.model.NovelNotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

        fun updateNovelNotificationSetting(novelId: Long) {
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
            saveJob = viewModelScope.launch {
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
