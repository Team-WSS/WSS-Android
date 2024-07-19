package com.teamwss.websoso.ui.detailExplore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailExploreViewModel @Inject constructor() : ViewModel() {
    private val _selectedGenres: MutableLiveData<MutableList<String>> =
        MutableLiveData(mutableListOf())
    val selectedGenres: LiveData<List<String>> get() = _selectedGenres.map { it.toList() }

    private val _selectedSeriesStatus: MutableLiveData<String?> = MutableLiveData()
    val selectedStatus: LiveData<String?> get() = _selectedSeriesStatus

    private val _selectedRating: MutableLiveData<Float?> = MutableLiveData()
    val selectedRating: LiveData<Float?> get() = _selectedRating

    fun updateSelectedGenres(genre: String) {
        if (selectedGenres.value?.contains(genre) == true) {
            _selectedGenres.value?.remove(genre)
        } else {
            _selectedGenres.value?.add(genre)
        }

        Log.d("123123", selectedGenres.value.toString())
    }

    fun updateSelectedSeriesStatus(status: String?) {
        _selectedSeriesStatus.value = status
    }

    fun updateSelectedRating(rating: Float?) {
        _selectedRating.value = rating
    }
}