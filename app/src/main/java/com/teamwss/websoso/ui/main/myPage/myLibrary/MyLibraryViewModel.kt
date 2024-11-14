package com.teamwss.websoso.ui.main.myPage.myLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myLibrary.model.AttractivePoints
import com.teamwss.websoso.ui.main.myPage.myLibrary.model.MyLibraryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyLibraryViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(MyLibraryUiState())
    val uiState: LiveData<MyLibraryUiState> get() = _uiState

    private val _attractivePointsText = MutableLiveData<String>()
    val attractivePointsText: LiveData<String> get() = _attractivePointsText

    var userId: Long = -1
        private set

    init {
        updateMyLibrary()
    }

   fun updateMyLibrary(){
        viewModelScope.launch {
            userId = userRepository.fetchUserId()
            updateNovelStats(userId)
            updateGenrePreference(userId)
            updateNovelPreferences(userId)
        }
    }

    private fun updateNovelStats(userId: Long) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserNovelStats(userId)
            }.onSuccess { novelStats ->
                _uiState.value = _uiState.value?.copy(
                    novelStats = novelStats,
                    isError = false,
                    isLoading = false,
                )
            }.onFailure {
                _uiState.value = _uiState.value?.copy(isLoading = false, isError = true)
            }
        }
    }

    fun hasNoPreferences(): Boolean {
        return _uiState.value?.novelStats?.run {
            interestNovelCount == 0 && watchingNovelCount == 0 &&
                    watchedNovelCount == 0 && quitNovelCount == 0
        } ?: true
    }

    private fun updateGenrePreference(userId: Long) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchGenrePreference(userId)
            }.onSuccess { genres ->
                val sortedGenres = genres.sortedByDescending { it.genreCount }
                _uiState.value = _uiState.value?.copy(
                    topGenres = sortedGenres.take(TOP_GENRE_COUNT),
                    restGenres = sortedGenres.drop(TOP_GENRE_COUNT),
                    isError = false,
                )
            }.onFailure {
                _uiState.value = _uiState.value?.copy(isError = true)
            }
        }
    }

    fun updateToggleGenresVisibility() {
        _uiState.value = _uiState.value?.copy(
            isGenreListVisible = _uiState.value?.isGenreListVisible?.not() ?: false
        )
    }

    private fun updateNovelPreferences(userId: Long) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchNovelPreferences(userId)
            }.onSuccess { novelPreferences ->
                _uiState.value = _uiState.value?.copy(
                    novelPreferences = novelPreferences,
                    translatedAttractivePoints = translateAttractivePoints(novelPreferences.attractivePoints),
                    isError = false,
                )
            }.onFailure {
                _uiState.value = _uiState.value?.copy(isError = true)
            }
        }
    }

    private fun translateAttractivePoints(attractivePoints: List<String>): List<String> {
        return attractivePoints.mapNotNull { point ->
            AttractivePoints.fromString(point)?.korean
        }
    }

    companion object {
        private const val TOP_GENRE_COUNT = 3
    }
}
