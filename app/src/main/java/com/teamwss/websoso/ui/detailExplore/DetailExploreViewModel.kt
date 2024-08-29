package com.teamwss.websoso.ui.detailExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.common.ui.model.CategoriesModel
import com.teamwss.websoso.data.repository.FakeKeywordRepository
import com.teamwss.websoso.ui.detailExplore.info.model.Genre
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordUiState
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailExploreViewModel @Inject constructor(
    private val fakeKeywordRepository: FakeKeywordRepository,
) : ViewModel() {
    private val _selectedGenres: MutableLiveData<MutableList<Genre>> =
        MutableLiveData(mutableListOf())

    private val _selectedSeriesStatus: MutableLiveData<String?> = MutableLiveData()
    val selectedStatus: LiveData<String?> get() = _selectedSeriesStatus

    private val _selectedRating: MutableLiveData<Float?> = MutableLiveData()
    val selectedRating: LiveData<Float?> get() = _selectedRating

    val ratings: List<Float> = listOf(3.5f, 4.0f, 4.5f, 4.8f)

    private val _isInfoChipSelected: MediatorLiveData<Boolean> = MediatorLiveData()
    val isInfoChipSelected: LiveData<Boolean> get() = _isInfoChipSelected

    private val _uiState: MutableLiveData<DetailExploreKeywordUiState> =
        MutableLiveData(DetailExploreKeywordUiState())
    val uiState: LiveData<DetailExploreKeywordUiState> get() = _uiState

    private val _searchWord: MutableLiveData<String> = MutableLiveData()
    val searchWord: MutableLiveData<String> get() = _searchWord

    private val _isSearchCancelButtonVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSearchCancelButtonVisibility: LiveData<Boolean> get() = _isSearchCancelButtonVisibility

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

    private fun isEnabled(): Boolean {
        val isGenreChipSelected: Boolean = _selectedGenres.value?.isNotEmpty() == true
        val isStatusChipSelected: Boolean = _selectedSeriesStatus.value.isNullOrEmpty().not()
        val isRatingChipSelected: Boolean = _selectedRating.value != null

        return isGenreChipSelected || isStatusChipSelected || isRatingChipSelected
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

    fun updateSearchCancelButtonVisibility() {
        _isSearchCancelButtonVisibility.value = _searchWord.value.isNullOrEmpty().not()
    }

    fun updateSearchWordEmpty() {
        _searchWord.value = ""
    }

    fun updateKeywords() {
        viewModelScope.launch {
            runCatching {
                fakeKeywordRepository.fetchKeyword()
            }.onSuccess { keywordsList ->
                val categoriesModel = CategoriesModel(
                    categories = keywordsList.map { it.toUi() }
                )

                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    categories = categoriesModel.categories
                )

            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun updateClickedChipState(keywordId: Int) {
        val currentUiState = _uiState.value ?: return

        val updatedCategories = currentUiState.categories.map { category ->
            val updatedKeywords = category.keywords.map { existingKeyword ->
                when (existingKeyword.keywordId == keywordId) {
                    true -> existingKeyword.copy(isSelected = !existingKeyword.isSelected)
                    false -> existingKeyword
                }
            }
            category.copy(keywords = updatedKeywords)
        }
        _uiState.value = currentUiState.copy(categories = updatedCategories)
    }
}
