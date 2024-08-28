package com.teamwss.websoso.ui.myPage.myLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.data.model.NovelPreferenceEntity
import com.teamwss.websoso.data.repository.FakeUserRepository
import com.teamwss.websoso.ui.myPage.myLibrary.model.AttractivePoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyLibraryViewModel @Inject constructor(private val fakeUserRepository: FakeUserRepository) :
    ViewModel() {
    private val _genres = MutableLiveData<List<GenrePreferenceEntity>>()
    val genres: LiveData<List<GenrePreferenceEntity>> = _genres

    private val _isGenreListVisible = MutableLiveData<Boolean>(false)
    val isGenreListVisible: LiveData<Boolean> = _isGenreListVisible

    private val _novelPreferences = MutableLiveData<NovelPreferenceEntity>()
    val novelPreferences: LiveData<NovelPreferenceEntity> = _novelPreferences

    private val _attractivePointsText = MutableLiveData<String>()
    val attractivePointsText: LiveData<String> = _attractivePointsText

    init {
        updatePreferenceData()
        setDummyData()
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
