package com.teamwss.websoso.ui.novelDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.data.repository.UserNovelRepository
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelDetail.model.NovelDetailModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelDetailViewModel @Inject constructor(
    private val novelRepository: NovelRepository,
    private val userNovelRepository: UserNovelRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _novelDetailModel = MutableLiveData<NovelDetailModel>(NovelDetailModel())
    val novelDetailModel: LiveData<NovelDetailModel> get() = _novelDetailModel
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading
    private val _error = MutableLiveData<Boolean>(false)
    val error: LiveData<Boolean> get() = _error

    init {
        updateLoginStatus()
    }

    private fun updateLoginStatus() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchIsLogin()
            }.onSuccess { isLogin ->
                //TODO: _novelDetailModel.value = novelDetailModel.value?.copy(isLogin = isLogin)
                _novelDetailModel.value = novelDetailModel.value?.copy(isLogin = true)
            }.onFailure {
                throw it
            }
        }
    }

    fun updateNovelDetail(novelId: Long) {
        if (loading.value == true) return
        viewModelScope.launch {
            runCatching {
                _loading.value = true
                novelRepository.getNovelDetail(novelId)
            }.onSuccess { novelDetail ->
                _novelDetailModel.value = novelDetail.toUi(novelId)
                _loading.value = false
                if (novelDetailModel.value?.userNovel?.isAlreadyRated == false) {
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
                userRepository.fetchNovelDetailFirstLaunched()
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
                userRepository.saveNovelDetailFirstLaunched(false)
            }.onSuccess {
                _novelDetailModel.value =
                    novelDetailModel.value?.copy(isFirstLaunched = false)
            }.onFailure {
                _novelDetailModel.value = novelDetailModel.value?.copy(isFirstLaunched = true)
            }
        }
    }

    fun updateGenreImage(genreImage: String) {
        _novelDetailModel.value = novelDetailModel.value?.copy(
            novel = novelDetailModel.value?.novel?.copy(
                novelGenreImage = genreImage,
            ) ?: return
        )
    }
}
