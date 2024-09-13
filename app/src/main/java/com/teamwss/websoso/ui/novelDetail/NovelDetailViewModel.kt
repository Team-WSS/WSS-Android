package com.teamwss.websoso.ui.novelDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.data.repository.UserNovelRepository
import com.teamwss.websoso.data.datasource.UserPreferencesDataSource
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelDetail.model.NovelDetailModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelDetailViewModel @Inject constructor(
    private val novelRepository: NovelRepository,
    private val userNovelRepository: UserNovelRepository,
    private val userPreferencesDataSource: UserPreferencesDataSource,
) : ViewModel() {

    private val _novelDetailModel = MutableLiveData<NovelDetailModel>()
    val novelDetailModel: LiveData<NovelDetailModel> get() = _novelDetailModel
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading
    private val _error = MutableLiveData<Boolean>(false)
    val error: LiveData<Boolean> get() = _error

    fun updateNovelDetail(novelId: Long) {
        if (loading.value == true) return
        viewModelScope.launch {
            runCatching {
                _loading.value = true
                novelRepository.getNovelDetail(novelId)
            }.onSuccess { novelDetail ->
                _novelDetailModel.value = novelDetail.toUi(novelId)
                _loading.value = false
                if (novelDetailModel.value?.userNovel?.isAlreadyPartiallyRated == false && novelDetailModel.value?.userNovel?.isAlreadyAllRated == false) {
                    checkIsFirstLaunched()
                }
            }.onFailure {
                _error.value = true
                _loading.value = false
            }
        }
    }

    fun updateUserInterest(novelId: Long) {
        viewModelScope.launch {
            runCatching {
                _novelDetailModel.value = novelDetailModel.value?.copy(
                    userNovel = novelDetailModel.value?.userNovel?.copy(
                        isUserNovelInterest = novelDetailModel.value?.userNovel?.isUserNovelInterest?.not()
                            ?: false
                    ) ?: return@runCatching
                )
                novelRepository.saveUserInterest(
                    novelId,
                    novelDetailModel.value?.userNovel?.isUserNovelInterest ?: false
                )
            }.onFailure {
                _novelDetailModel.value = novelDetailModel.value?.copy(
                    userNovel = novelDetailModel.value?.userNovel?.copy(
                        isUserNovelInterest = novelDetailModel.value?.userNovel?.isUserNovelInterest?.not()
                            ?: false
                    ) ?: return@onFailure
                )
            }
        }
    }

    fun deleteUserNovel(novelId: Long) {
        viewModelScope.launch {
            runCatching {
                userNovelRepository.deleteUserNovel(novelId)
            }.onSuccess {
                updateNovelDetail(novelId)
            }.onFailure {}
        }
    }

    private fun checkIsFirstLaunched() {
        viewModelScope.launch {
            runCatching {
                userPreferencesDataSource.fetchNovelDetailFirstLaunched()
            }.onSuccess { isFirstLaunched ->
                _novelDetailModel.value =
                    novelDetailModel.value?.copy(isFirstLaunched = isFirstLaunched)
            }.onFailure {
                _novelDetailModel.value = novelDetailModel.value?.copy(isFirstLaunched = true)
            }
        }
    }

    fun updateIsFirstLaunched() {
        viewModelScope.launch {
            runCatching {
                userPreferencesDataSource.saveNovelDetailFirstLaunched(false)
            }.onSuccess {
                _novelDetailModel.value =
                    novelDetailModel.value?.copy(isFirstLaunched = false)
            }.onFailure {
                _novelDetailModel.value = novelDetailModel.value?.copy(isFirstLaunched = true)
            }
        }
    }
}
