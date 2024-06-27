package com.teamwss.websoso.ui.novelDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.domain.usecase.GetNovelDetailUseCase
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelDetail.model.NovelDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelDetailViewModel @Inject constructor(
    private val getNovelDetailUseCase: GetNovelDetailUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<NovelDetailUiState> =
        MutableLiveData(NovelDetailUiState())
    val uiState: LiveData<NovelDetailUiState> get() = _uiState

    fun fetchNovelDetail(novelId: Long) {
        viewModelScope.launch {
            runCatching {
                getNovelDetailUseCase(novelId)
            }.onSuccess { novelDetail ->
                _uiState.value =
                    NovelDetailUiState(
                        loading = false,
                        novelDetail = novelDetail.toUi(),
                    )
            }.onFailure {
                _uiState.value = NovelDetailUiState(loading = false, error = true)
            }
        }
    }
}
