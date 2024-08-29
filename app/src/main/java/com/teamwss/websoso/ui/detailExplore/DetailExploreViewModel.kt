package com.teamwss.websoso.ui.detailExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

    private val _isInfoChipSelected: MediatorLiveData<Boolean> = MediatorLiveData()
    val isInfoChipSelected: LiveData<Boolean> get() = _isInfoChipSelected

    init {
        _isInfoChipSelected.addSource(_selectedGenres) {
            _isInfoChipSelected.value = isEnabled()
        }
        _isInfoChipSelected.addSource(_selectedSeriesStatus) {
            _isInfoChipSelected.value = isEnabled()
        }
        _isInfoChipSelected.addSource(_selectedRating) {
            _isInfoChipSelected.value = isEnabled()
        }
    }

    fun updateSelectedGenres(genre: Genre) {
        val currentGenres = _selectedGenres.value?.toMutableList() ?: mutableListOf()

        _selectedGenres.value = when (currentGenres.contains(genre)) {
            true -> {
                currentGenres.remove(genre)
                currentGenres
            }

            false -> {
                currentGenres.add(genre)
                currentGenres
            }
        }
    }

    fun updateSelectedSeriesStatus(status: String?) {
        _selectedSeriesStatus.value = status
    }

    fun updateSelectedRating(rating: Float?) {
        _selectedRating.value = rating
    }

    private fun isEnabled(): Boolean {
        val isGenreChipSelected: Boolean = _selectedGenres.value?.isNotEmpty() == true
        val isStatusChipSelected: Boolean = _selectedSeriesStatus.value.isNullOrEmpty().not()
        val isRatingChipSelected: Boolean = _selectedRating.value != null

        return isGenreChipSelected || isStatusChipSelected || isRatingChipSelected
    }
}
