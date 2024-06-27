package com.teamwss.websoso.ui.novelDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FakeNovelDetailRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelDetail.model.NovelDetailModel
import com.teamwss.websoso.ui.novelDetail.model.NovelDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelDetailViewModel @Inject constructor(
    private val fakeNovelDetailRepository: FakeNovelDetailRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<NovelDetailUiState> =
        MutableLiveData(NovelDetailUiState.Loading)
    val uiState: LiveData<NovelDetailUiState> get() = _uiState
    val novelDetail: NovelDetailModel?
        get() = (_uiState.value as? NovelDetailUiState.Success)?.novelDetail

    init {
        _uiState.value = NovelDetailUiState.Loading
    }

    fun updateNovelDetail(novelId: Long) {
        viewModelScope.launch {
            runCatching {
                fakeNovelDetailRepository.fetchNovelDetail(novelId)
            }.onSuccess { novelDetail ->
                _uiState.value = NovelDetailUiState.Success(novelDetail.toUi())
            }.onFailure {
                _uiState.value = NovelDetailUiState.Error
            }
        }
    }
}
