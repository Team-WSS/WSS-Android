package com.teamwss.websoso.ui.myPage.myLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.data.model.AttractivePointEntity
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.data.repository.MyLibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyLibraryViewModel @Inject constructor(private val myLibraryRepository: MyLibraryRepository) :
    ViewModel() {
    private val _genres = MutableLiveData<List<GenrePreferenceEntity>>()
    val genres: LiveData<List<GenrePreferenceEntity>> = _genres

    private val _isGenreListVisible = MutableLiveData<Boolean>().apply { value = false }
    val isGenreListVisible: LiveData<Boolean> = _isGenreListVisible

    private val _attractivePoints = MutableLiveData<List<AttractivePointEntity>>()
    val attractivePoints: LiveData<List<AttractivePointEntity>> = _attractivePoints

    private val _attractivePointsText = MutableLiveData<String>()
    val attractivePointsText: LiveData<String> get() = _attractivePointsText

    init {
        loadPrefrenceData()
        setDummyData()
    }

    private fun loadPrefrenceData() {
        _genres.value = myLibraryRepository.getGenres()
        _attractivePoints.value = myLibraryRepository.getAttractivePoints()
    }

    private fun translateAttractivePointsText(text: String): String {
        return text.split(", ").joinToString(", ") {
            when (it) {
                "character" -> "캐릭터"
                "relationship" -> "관계"
                "material" -> "소재"
                else -> it
            }
        }
    }

    fun updateToggleGenresVisibility() {
        _isGenreListVisible.value = _isGenreListVisible.value?.not() ?: false
    }

    fun setText(serverText: String, fixedText: String) {
        val translatedText = translateAttractivePointsText(serverText)
        _attractivePointsText.value = "$translatedText$fixedText"
    }

    private fun setDummyData() {
        val serverText = "character, material"
        val fixedText = "가 매력적인 작품"
        setText(serverText, fixedText)
    }
}