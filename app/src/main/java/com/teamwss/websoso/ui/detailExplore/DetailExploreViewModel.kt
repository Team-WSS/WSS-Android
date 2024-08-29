package com.teamwss.websoso.ui.detailExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.common.ui.model.CategoriesModel
import com.teamwss.websoso.data.repository.KeywordRepository
import com.teamwss.websoso.ui.detailExplore.info.model.Genre
import com.teamwss.websoso.ui.detailExplore.info.model.SeriesStatus
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordUiState
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailExploreViewModel @Inject constructor(
    private val keywordRepository: KeywordRepository,
) : ViewModel() {
    private val _selectedGenres: MutableLiveData<MutableList<Genre>> =
        MutableLiveData(mutableListOf())
    val selectedGenres: LiveData<List<Genre>> get() = _selectedGenres.map { it.toList() }

    private val _selectedSeriesStatus: MutableLiveData<SeriesStatus?> = MutableLiveData()
    val selectedStatus: LiveData<SeriesStatus?> get() = _selectedSeriesStatus

    private val _selectedRating: MutableLiveData<Float?> = MutableLiveData()
    val selectedRating: LiveData<Float?> get() = _selectedRating

    val ratings: List<Float> = listOf(3.5f, 4.0f, 4.5f, 4.8f)

    private val _isInfoChipSelected: MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isInfoChipSelected: LiveData<Boolean> get() = _isInfoChipSelected

    private val _isKeywordChipSelected: MutableLiveData<Boolean> = MutableLiveData(false)
    val isKeywordChipSelected: LiveData<Boolean> get() = _isKeywordChipSelected

    private val _uiState: MutableLiveData<DetailExploreKeywordUiState> =
        MutableLiveData(DetailExploreKeywordUiState())
    val uiState: LiveData<DetailExploreKeywordUiState> get() = _uiState

    init {
        _isInfoChipSelected.addSource(_selectedGenres) {
            _isInfoChipSelected.value = isInfoChipSelectedEnabled()
        }
        _isInfoChipSelected.addSource(_selectedSeriesStatus) {
            _isInfoChipSelected.value = isInfoChipSelectedEnabled()
        }
        _isInfoChipSelected.addSource(_selectedRating) {
            _isInfoChipSelected.value = isInfoChipSelectedEnabled()
        }

        updateKeyword(null)
    }

    private fun isInfoChipSelectedEnabled(): Boolean {
        val isGenreChipSelected: Boolean = _selectedGenres.value?.isNotEmpty() == true
        val isStatusChipSelected: Boolean = _selectedSeriesStatus.value != null
        val isRatingChipSelected: Boolean = _selectedRating.value != null

        return isGenreChipSelected || isStatusChipSelected || isRatingChipSelected
    }

    fun updateSelectedInfoValueClear() {
        _selectedGenres.value = mutableListOf()
        _selectedSeriesStatus.value = null
        _selectedRating.value = null
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

    fun updateSelectedSeriesStatus(status: SeriesStatus?) {
        _selectedSeriesStatus.value = status
    }

    fun updateSelectedRating(rating: Float?) {
        _selectedRating.value = rating
    }

    fun updateKeyword(searchWord: String?) {
        viewModelScope.launch {
            runCatching {
                keywordRepository.fetchKeywords(searchWord)
            }.onSuccess { keywordsList ->
                val categoriesModel = CategoriesModel(
                    categories = keywordsList.categories.map { it.toUi() },
                )

                when (searchWord == null) {
                    true -> {
                        _uiState.value = uiState.value?.copy(
                            loading = false,
                            categories = categoriesModel.categories,
                        )
                    }

                    false -> {
                        val results = categoriesModel.categories.flatMap { it.keywords }

                        _uiState.value = uiState.value?.copy(
                            loading = false,
                            searchResultKeywords = results,
                            isInitialSearchKeyword = false,
                            isSearchResultKeywordsEmpty = results.isEmpty(),
                        )
                    }
                }
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun updateSelectedKeywordValueClear() {
        val currentState = _uiState.value ?: return
        val updatedCategories = currentState.categories

        val resetCategories = updatedCategories.map { category ->
            category.copy(keywords = category.keywords.map { keyword ->
                keyword.copy(isSelected = false)
            })
        }

        _uiState.value = currentState.copy(categories = resetCategories)
        _isKeywordChipSelected.value = false
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

        val isAnyKeywordSelected = updatedCategories.any { category ->
            category.keywords.any { it.isSelected }
        }

        _isKeywordChipSelected.value = isAnyKeywordSelected
        _uiState.value = currentUiState.copy(categories = updatedCategories)
    }

    fun updateIsSearchKeywordProceeding(isProceeding: Boolean) {
        uiState.value?.let { uiState ->
            _uiState.value = uiState.copy(
                isSearchKeywordProceeding = isProceeding,
            )
        }
    }

    fun initSearchKeyword() {
        uiState.value?.let { uiState ->
            _uiState.value = uiState.copy(
                searchResultKeywords = emptyList(),
                isSearchKeywordProceeding = false,
                isInitialSearchKeyword = true,
                isSearchResultKeywordsEmpty = false,
            )
        }
    }
}
