package com.teamwss.websoso.ui.novelDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FakeNovelDetailRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelDetail.model.NovelDetailModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelDetailViewModel @Inject constructor(
    private val fakeNovelDetailRepository: FakeNovelDetailRepository,
) : ViewModel() {
    private val _novelDetail = MutableLiveData<NovelDetailModel>()
    val novelDetail: LiveData<NovelDetailModel> get() = _novelDetail
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading
    private val _error = MutableLiveData<Boolean>(false)
    val error: LiveData<Boolean> get() = _error

    fun updateNovelDetail(novelId: Long) {
        viewModelScope.launch {
            runCatching {
                fakeNovelDetailRepository.fetchNovelDetail(novelId)
            }.onSuccess { novelDetail ->
                _loading.value = false
                _novelDetail.value = novelDetail.toUi()
            }.onFailure {
                _error.value = true
            }
        }
    }
}
