package com.teamwss.websoso.ui.main.myPage.myLibrary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.data.model.NovelPreferenceEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.data.repository.FakeUserRepository
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myLibrary.model.AttractivePoints
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyLibraryViewModel @Inject constructor(
    private val fakeUserRepository: FakeUserRepository,
    private val userRepository: UserRepository
) :
    ViewModel() {
    private val _genres = MutableLiveData<List<GenrePreferenceEntity>>()
    val genres: LiveData<List<GenrePreferenceEntity>> = _genres

    private val _isGenreListVisible = MutableLiveData<Boolean>(false)
    val isGenreListVisible: LiveData<Boolean> = _isGenreListVisible

    private val _novelPreferences = MutableLiveData<NovelPreferenceEntity>()
    val novelPreferences: LiveData<NovelPreferenceEntity> = _novelPreferences

    private val _attractivePointsText = MutableLiveData<String>()
    val attractivePointsText: LiveData<String> = _attractivePointsText

    private val _novelStats = MutableLiveData<UserNovelStatsEntity>()
    val novelStats: LiveData<UserNovelStatsEntity> get() = _novelStats

    init {
        updateNovelStats()
        updatePreferenceData()
        setDummyData()
    }

    private fun updateNovelStats() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserNovelStats()
            }.onSuccess { novelStats ->
                Log.d("MyLibraryFragment", "Novel Stats: $novelStats")
                _novelStats.value = novelStats
            }.onFailure { exception ->
                Log.e("NovelViewModel", "Failed to load novel counts", exception)
            }
        }
    }

    private fun updatePreferenceData() {
        _genres.value = fakeUserRepository.getGenres()
        _novelPreferences.value = fakeUserRepository.getNovelPreferences()
    }

    fun updateToggleGenresVisibility() {
        _isGenreListVisible.value = _isGenreListVisible.value?.not() ?: false
    }

    fun setAttractivePointText(serverText: String, fixedText: String) {
        val translatedText = translateAttractivePointsText(serverText)
        _attractivePointsText.value = "$translatedText$fixedText"
    }

    private fun translateAttractivePointsText(text: String): String {
        return text.split(", ").joinToString(", ") { point ->
            AttractivePoints.fromString(point)?.korean ?: point
        }
    }

    private fun setDummyData() {
        val dummyPreferences = fakeUserRepository.getNovelPreferences()

        val serverText = dummyPreferences.attractivePoint.joinToString(", ")
        val fixedText = "가 매력적인 작품"

        setAttractivePointText(serverText, fixedText)
    }
}
