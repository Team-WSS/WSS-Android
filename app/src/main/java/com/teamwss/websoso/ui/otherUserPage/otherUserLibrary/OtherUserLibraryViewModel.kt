package com.teamwss.websoso.ui.otherUserPage.otherUserLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myLibrary.model.AttractivePoints
import com.teamwss.websoso.ui.otherUserPage.otherUserLibrary.model.OtherUserLibraryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserLibraryViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(OtherUserLibraryUiState())
    val uiState: LiveData<OtherUserLibraryUiState> get() = _uiState

    private val _attractivePointsText = MutableLiveData<String>()
    val attractivePointsText: LiveData<String> get() = _attractivePointsText

    private val _userId = MutableLiveData<Long>()
    val userId: LiveData<Long> get() = _userId

    fun updateUserId(userId: Long) {
        _userId.value = userId
        _uiState.value = _uiState.value?.copy(isLoading = true)

        viewModelScope.launch {
            val novelStatsResult = runCatching { updateNovelStats(userId) }
            val genrePreferenceResult = runCatching { updateGenrePreference(userId) }
            val novelPreferencesResult = runCatching { updateNovelPreferences(userId) }

            when {
                novelStatsResult.isSuccess && genrePreferenceResult.isSuccess && novelPreferencesResult.isSuccess -> {
                    _uiState.value = _uiState.value?.copy(isLoading = false, error = false)
                }
                else -> {
                    _uiState.value = _uiState.value?.copy(isLoading = false, error = true)
                }
            }
        }
    }

    private fun updateNovelStats(userId: Long) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserNovelStats(userId)
            }.onSuccess { novelStats ->
                _uiState.value = _uiState.value?.copy(
                    novelStats = novelStats,
                    isLoading = false,
                )
            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    error = true,
                )
            }
        }
    }

    fun hasNoPreferences(): Boolean {
        val stats = _uiState.value?.novelStats ?: return true
        return stats.interestNovelCount == 0 &&
                stats.watchingNovelCount == 0 &&
                stats.watchedNovelCount == 0 &&
                stats.quitNovelCount == 0
    }

    private fun updateGenrePreference(userId: Long) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchGenrePreference(userId)
            }.onSuccess { genres ->
                val sortedGenres = genres.sortedByDescending { it.genreCount }
                _uiState.value = _uiState.value?.copy(
                    topGenres = sortedGenres.take(3),
                    restGenres = sortedGenres.drop(3),
                )
            }.onFailure {
                _uiState.value = _uiState.value?.copy(error = true)
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
                )
            }.onFailure {
                _uiState.value = _uiState.value?.copy(error = true)
            }
        }
    }

    private fun translateAttractivePoints(attractivePoints: List<String>): List<String> {
        return attractivePoints.mapNotNull { point ->
            AttractivePoints.fromString(point)?.korean
        }
    }
}