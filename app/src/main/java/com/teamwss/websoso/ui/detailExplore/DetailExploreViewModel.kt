package com.teamwss.websoso.ui.detailExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailExploreViewModel @Inject constructor() : ViewModel() {
    private val _selectedGenres: MutableLiveData<List<String>?> = MutableLiveData()
    val selectedGenres: LiveData<List<String>?> get() = _selectedGenres

    private val _selectedSeriesStatus: MutableLiveData<String?> = MutableLiveData()
    val selectedStatus: LiveData<String?> get() = _selectedSeriesStatus

    private val _selectedRating: MutableLiveData<Float?> = MutableLiveData()
    val selectedRating: LiveData<Float?> get() = _selectedRating

    fun updateSelectedGenres(genre: String) {
        val currentGenres = _selectedGenres.value.orEmpty()
        val updatedGenres = if (currentGenres.contains(genre)) {
            currentGenres.filter { it != genre }
        } else {
            currentGenres + genre
        }

        _selectedGenres.value = updatedGenres
    }

    fun updateSelectedSeriesStatus(status: String?) {
        _selectedSeriesStatus.value = status
    }

    fun updateSelectedRating(rating: Float?) {
        _selectedRating.value = rating
    }
}