package com.teamwss.websoso.ui.novelInfo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FakeNovelInfoRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelInfo.model.ExpandTextUiModel.Companion.DEFAULT_BODY_MAX_LINES
import com.teamwss.websoso.ui.novelInfo.model.NovelInfoUiState
import com.teamwss.websoso.ui.novelInfo.model.PlatformsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelInfoViewModel @Inject constructor(
    private val novelInfoRepository: FakeNovelInfoRepository,
) : ViewModel() {

    private val _uiState: MutableLiveData<NovelInfoUiState> = MutableLiveData(NovelInfoUiState())
    val uiState: LiveData<NovelInfoUiState> get() = _uiState

    fun updateNovelInfo(novelId: Long) {
        viewModelScope.launch {
            runCatching {
                novelInfoRepository.fetchNovelInfo(novelId)
            }.onSuccess { novelInfo ->
                _uiState.value = uiState.value?.copy(
                    novelInfoModel = novelInfo.toUi(),
                    platforms = uiState.value?.platforms?.formatPlatforms(novelInfo.platforms) ?: PlatformsModel(),
                    keywords = novelInfo.keywords.map { it.toUi() },
                    loading = false,
                )
            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun updateExpandTextToggle() {
        _uiState.value?.let { currentState ->
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
        _uiState.value?.let { currentState ->
            val expandTextUiModel = currentState.expandTextModel
            val updatedExpandTextUiModel = expandTextUiModel.copy(
                expandTextToggleVisibility = lineCount >= DEFAULT_BODY_MAX_LINES && ellipsisCount > 0,
                isExpandTextToggleSelected = false
            )
            _uiState.value = currentState.copy(expandTextModel = updatedExpandTextUiModel)
        }
    }
}
