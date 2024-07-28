package com.teamwss.websoso.ui.novelDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.data.repository.UserNovelRepository
import com.teamwss.websoso.domain.usecase.GetNovelDetailUseCase
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelDetail.model.NovelDetailModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelDetailViewModel @Inject constructor(
    private val novelDetailUseCase: GetNovelDetailUseCase,
    private val novelRepository: NovelRepository,
    private val userNovelRepository: UserNovelRepository,
) : ViewModel() {

    private val _novelDetail = MutableLiveData<NovelDetailModel>()
    val novelDetail: LiveData<NovelDetailModel> get() = _novelDetail
    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean> get() = _loading
    private val _error = MutableLiveData<Boolean>(false)
    val error: LiveData<Boolean> get() = _error

    fun updateNovelDetail(novelId: Long) {
        viewModelScope.launch {
            runCatching {
                novelDetailUseCase.invoke(novelId)
            }.onSuccess { novelDetail ->
                _loading.value = false
                _novelDetail.value = novelDetail.toUi(novelId)
            }.onFailure {
                _loading.value = false
                _error.value = true
            }
        }
    }

    fun updateUserInterest(novelId: Long) {
        viewModelScope.launch {
            runCatching {
                when (novelDetail.value?.userNovel?.isUserNovelInterest == true) {
                    true -> novelRepository.deleteUserInterest(novelId)
                    else -> novelRepository.postUserInterest(novelId)
                }
            }.onSuccess {
                _novelDetail.value = novelDetail.value?.copy(
                    userNovel = novelDetail.value?.userNovel?.copy(
                        isUserNovelInterest = novelDetail.value?.userNovel?.isUserNovelInterest?.not() ?: false
                    ) ?: return@onSuccess
                )
            }.onFailure {}
        }
    }

    fun deleteUserNovel(novelId: Long) {
        viewModelScope.launch {
            runCatching {
                userNovelRepository.deleteUserNovel(novelId)
            }.onSuccess {
                _novelDetail.value = novelDetail.value?.copy(
                    userNovel = novelDetail.value?.defaultUserNovelModel ?: return@onSuccess
                )
            }.onFailure {}
        }
    }
}
