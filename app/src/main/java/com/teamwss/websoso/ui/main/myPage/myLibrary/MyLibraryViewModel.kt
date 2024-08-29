package com.teamwss.websoso.ui.main.myPage.myLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.data.model.NovelPreferenceEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myLibrary.model.AttractivePoints
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyLibraryViewModel @Inject constructor(
    private val userRepository: UserRepository,
) :
    ViewModel() {
    private val _genres = MutableLiveData<List<GenrePreferenceEntity>>()
    val genres: LiveData<List<GenrePreferenceEntity>> get()= _genres

    private val _dominantGenres = MutableLiveData<List<GenrePreferenceEntity>>()
    val topGenres: LiveData<List<GenrePreferenceEntity>> get()= _dominantGenres

    private val _restGenres = MutableLiveData<List<GenrePreferenceEntity>>()
    val restGenres: LiveData<List<GenrePreferenceEntity>> get()= _restGenres

    private val _isGenreListVisible = MutableLiveData<Boolean>(false)
    val isGenreListVisible: LiveData<Boolean> get()= _isGenreListVisible

    private val _novelPreferences = MutableLiveData<NovelPreferenceEntity>()
    val novelPreferences: LiveData<NovelPreferenceEntity> get()= _novelPreferences

    private val _attractivePointsText = MutableLiveData<String>()
    val attractivePointsText: LiveData<String> get() = _attractivePointsText

    private val _translatedAttractivePoints = MutableLiveData<List<String>>()
    val translatedAttractivePoints: LiveData<List<String>> get() = _translatedAttractivePoints

    private val _novelStats = MutableLiveData<UserNovelStatsEntity>()
    val novelStats: LiveData<UserNovelStatsEntity> get() = _novelStats

    private val userId: Long = getUserId()

    init {
        updateNovelStats()
        updateGenrePreference(userId)
        updateNovelPreferences(userId)
    }

    private fun updateNovelStats() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserNovelStats()
            }.onSuccess { novelStats ->
                _novelStats.value = novelStats
            }.onFailure { exception ->
            }
        }
    }

    private fun updateGenrePreference(userId: Long) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchGenrePreference(userId)
            }.onSuccess { genres ->
                val sortedGenres = genres.sortedByDescending { it.genreCount }

                _dominantGenres.value = sortedGenres.take(3)
                _restGenres.value = sortedGenres.drop(3)
            }.onFailure { exception ->
            }
        }
    }

    private fun getUserId(): Long {
        return 1L
    }

    fun updateToggleGenresVisibility() {
        _isGenreListVisible.value = _isGenreListVisible.value?.not() ?: false
    }

    private fun updateNovelPreferences(userId: Long) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchNovelPreferences(userId)
            }.onSuccess { novelPreferences ->
                _novelPreferences.value = novelPreferences
                _translatedAttractivePoints.value = translateAttractivePoints(novelPreferences.attractivePoints)
            }.onFailure { exception ->
            }
        }
    }

    private fun translateAttractivePoints(attractivePoints: List<String>): List<String> {
        return attractivePoints.mapNotNull { point ->
            AttractivePoints.fromString(point)?.korean
        }
    }
}
