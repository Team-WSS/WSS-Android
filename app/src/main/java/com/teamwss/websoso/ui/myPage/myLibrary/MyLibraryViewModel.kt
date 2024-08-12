package com.teamwss.websoso.ui.myPage.myLibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.R
import com.teamwss.websoso.data.model.AttractivePointEntity
import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.data.repository.MyLibraryRepository
import com.teamwss.websoso.domain.model.AttractivePoints
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
        updatePreferenceData()
        setDummyData()
    }

    private fun updatePreferenceData() {
        _genres.value = myLibraryRepository.getGenres()
        _attractivePoints.value = myLibraryRepository.getAttractivePoints()
    }

    fun updateToggleGenresVisibility() {
        _isGenreListVisible.value = _isGenreListVisible.value?.not() ?: false
    }

    fun setText(serverText: String, fixedText: Int) {
        val translatedText = translateAttractivePointsText(serverText)
        _attractivePointsText.value = "$translatedText$fixedText"
    }

    private fun translateAttractivePointsText(text: String): String {
        return text.split(", ").joinToString(", ") { point ->
            AttractivePoints.fromString(point)?.korean ?: point
        }
    }

    private fun setDummyData() {
        val serverText = "character, material"
        val fixedText = R.string.my_library_attractive_point_fixed_text
        setText(serverText, fixedText)
    }
}