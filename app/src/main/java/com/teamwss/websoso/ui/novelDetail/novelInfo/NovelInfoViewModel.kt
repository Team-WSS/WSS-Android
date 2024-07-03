package com.teamwss.websoso.ui.novelDetail.novelInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FakeNovelInfoRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.ExpandTextUiModel.Companion.DEFAULT_BODY_MAX_LINES
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.NovelInfoUiState
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.PlatformsModel
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
                _uiState.value = _uiState.value?.copy(
                    novelInfoModel = novelInfo.toUi(),
                    platforms = PlatformsModel.formatPlatforms(novelInfo.platforms),
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
        _uiState.value?.let {
            val expandTextUiModel = it.expandTextModel
            when (!expandTextUiModel.isExpandTextToggleSelected) {
                true -> {
                    expandTextUiModel.isExpandTextToggleSelected = true
                    expandTextUiModel.bodyMaxLines = Int.MAX_VALUE
                }

                false -> {
                    expandTextUiModel.isExpandTextToggleSelected = false
                    expandTextUiModel.bodyMaxLines = DEFAULT_BODY_MAX_LINES
                }
            }
            _uiState.value = it.copy(expandTextModel = expandTextUiModel)
        }
    }

    fun updateExpandTextToggleVisibility(
        lineCount: Int,
        ellipsisCount: Int,
    ) {
        _uiState.value?.let {
            val expandTextUiModel = it.expandTextModel
            expandTextUiModel.expandTextToggleVisibility =
                lineCount >= DEFAULT_BODY_MAX_LINES && ellipsisCount > 0
            expandTextUiModel.isExpandTextToggleSelected = false
            _uiState.value = it.copy(expandTextModel = expandTextUiModel)
        }
    }
}
