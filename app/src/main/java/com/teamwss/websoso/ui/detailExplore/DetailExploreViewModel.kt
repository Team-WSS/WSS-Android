package com.teamwss.websoso.ui.detailExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.ui.detailExplore.info.model.Genre
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailExploreViewModel @Inject constructor() : ViewModel() {
    private val _selectedGenres: MutableLiveData<MutableList<Genre>> =
        MutableLiveData(mutableListOf())

    private val _selectedSeriesStatus: MutableLiveData<String?> = MutableLiveData()
    val selectedStatus: LiveData<String?> get() = _selectedSeriesStatus

    private val _selectedRating: MutableLiveData<Float?> = MutableLiveData()
    val selectedRating: LiveData<Float?> get() = _selectedRating

    val ratings: List<Float> = listOf(3.5f, 4.0f, 4.5f, 4.8f)

    private val _isInfoChipSelected: MutableLiveData<Boolean> = MutableLiveData()
    val isInfoChipSelected: LiveData<Boolean> get() = _isInfoChipSelected

    fun updateSelectedGenres(genre: Genre) {
        when (_selectedGenres.value?.contains(genre) ?: emptyList<Genre>()) {
            true -> _selectedGenres.value?.removeAll(listOf(genre))
            false -> _selectedGenres.value?.add(genre)
        }
        updateIsInfoChipSelected()
    }

    fun updateSelectedSeriesStatus(status: String?) {
        _selectedSeriesStatus.value = status
        updateIsInfoChipSelected()
    }

    fun updateSelectedRating(rating: Float?) {
        _selectedRating.value = rating
        updateIsInfoChipSelected()
    }

    private fun updateIsInfoChipSelected() {
        val isGenreChipSelected: Boolean = _selectedGenres.value?.isEmpty()?.not() ?: false
        val isStatusChipSelected: Boolean = _selectedSeriesStatus.value.isNullOrEmpty().not()
        val isRatingChipSelected: Boolean = _selectedRating.value != null

        _isInfoChipSelected.value =
            isGenreChipSelected || isStatusChipSelected || isRatingChipSelected
    }
}
