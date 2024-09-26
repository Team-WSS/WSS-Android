package com.teamwss.websoso.ui.novelInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelInfo.model.ExpandTextUiModel.Companion.DEFAULT_BODY_MAX_LINES
import com.teamwss.websoso.ui.novelInfo.model.NovelInfoUiState
import com.teamwss.websoso.ui.novelInfo.model.Platform
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelInfoViewModel @Inject constructor(
    private val novelRepository: NovelRepository,
) : ViewModel() {

    private val _uiState: MutableLiveData<NovelInfoUiState> = MutableLiveData(NovelInfoUiState())
    val uiState: LiveData<NovelInfoUiState> get() = _uiState

    fun updateNovelInfo(novelId: Long) {
        if (uiState.value?.loading == true) return
        viewModelScope.launch {
            runCatching {
                _uiState.value = uiState.value?.copy(loading = true, error = false)
                novelRepository.fetchNovelInfo(novelId)
            }.onSuccess { novelInfo ->
                _uiState.value = uiState.value?.copy(
                    novelInfoModel = novelInfo.toUi(),
                    platforms = novelInfo.platforms.map { it.toUi() },
                    keywords = novelInfo.keywords.map { it.toUi() },
                    isKeywordsExist = novelInfo.keywords.isNotEmpty(),
                    loading = false,
                    error = false,
                )
            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun updateNovelInfoWithDelay(novelId: Long) {
        viewModelScope.launch {
            delay(UPDATE_TASK_DELAY)
            updateNovelInfo(novelId)
        }
    }

    fun updateGraphHeight(viewHeight: Int) {
        uiState.value?.let { uiState ->
            _uiState.value = uiState.copy(
                novelInfoModel = uiState.novelInfoModel.copy(
                    unifiedReviewCount = uiState.novelInfoModel.unifiedReviewCount.formattedUnifiedReviewCount(
                        viewHeight
                    )
                )
            )
        }
    }

    fun updateExpandTextToggle() {
        uiState.value?.let { currentState ->
            val expandTextUiModel = currentState.expandTextModel
            val updatedExpandTextUiModel = when (!expandTextUiModel.isExpandTextToggleSelected) {
                true -> expandTextUiModel.copy(
                    isExpandTextToggleSelected = true,
                    bodyMaxLines = Int.MAX_VALUE
                )

                false -> expandTextUiModel.copy(
                    isExpandTextToggleSelected = false,
                    bodyMaxLines = DEFAULT_BODY_MAX_LINES
                )
            }
            _uiState.value = currentState.copy(expandTextModel = updatedExpandTextUiModel)
        }
    }

    fun updateExpandTextToggleVisibility(
        lineCount: Int,
        ellipsisCount: Int,
    ) {
        uiState.value?.let { currentState ->
            val expandTextUiModel = currentState.expandTextModel
            val updatedExpandTextUiModel = expandTextUiModel.copy(
                expandTextToggleVisibility = lineCount >= DEFAULT_BODY_MAX_LINES && ellipsisCount > 0,
                isExpandTextToggleSelected = false
            )
            _uiState.value = currentState.copy(expandTextModel = updatedExpandTextUiModel)
        }
    }

    fun updatePlatformImage(platform: Platform, platformImage: String) {
        uiState.value?.let { uiState ->
            val formattedPlatforms = uiState.platforms.map {
                when (it.platform == platform) {
                    true -> it.copy(platformImage = platformImage)
                    false -> it
                }
            }
            _uiState.value = uiState.copy(platforms = formattedPlatforms)
        }
    }

    companion object {
        private const val UPDATE_TASK_DELAY = 2000L
    }
}
