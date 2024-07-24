package com.teamwss.websoso.ui.detailExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailExploreViewModel @Inject constructor() : ViewModel() {
    private val _selectedGenres: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())

    private val _selectedSeriesStatus: MutableLiveData<String?> = MutableLiveData()
    val selectedStatus: LiveData<String?> get() = _selectedSeriesStatus

    private val _selectedRating: MutableLiveData<Float?> = MutableLiveData()
    val selectedRating: LiveData<Float?> get() = _selectedRating

    fun updateSelectedGenres(genre: String) {
        if (_selectedGenres.value?.remove(genre) == true) return
        _selectedGenres.value?.add(genre)
    }

    fun updateSelectedSeriesStatus(status: String?) {
        _selectedSeriesStatus.value = status
    }

    fun updateSelectedRating(rating: Float?) {
        _selectedRating.value = rating
    }
}