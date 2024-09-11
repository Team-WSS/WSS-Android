package com.teamwss.websoso.ui.detailExploreResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.common.ui.model.CategoriesModel
import com.teamwss.websoso.data.repository.KeywordRepository
import com.teamwss.websoso.domain.usecase.GetDetailExploreResultUseCase
import com.teamwss.websoso.ui.detailExplore.info.model.Genre
import com.teamwss.websoso.ui.detailExplore.info.model.SeriesStatus
import com.teamwss.websoso.ui.detailExploreResult.model.DetailExploreFilteredModel
import com.teamwss.websoso.ui.detailExploreResult.model.DetailExploreResultUiState
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailExploreResultViewModel @Inject constructor(
    private val getDetailExploreResultUseCase: GetDetailExploreResultUseCase,
    private val keywordRepository: KeywordRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<DetailExploreResultUiState> =
        MutableLiveData(DetailExploreResultUiState())
    val uiState: LiveData<DetailExploreResultUiState> get() = _uiState

    private val _selectedGenres: MutableLiveData<MutableList<Genre>?> =
        MutableLiveData(mutableListOf())
    val selectedGenres: LiveData<List<Genre>?> get() = _selectedGenres.map { it?.toList() }

    private val _isNovelCompleted: MutableLiveData<Boolean?> = MutableLiveData()
    val isNovelCompleted: LiveData<Boolean?> get() = _isNovelCompleted

    private val _selectedSeriesStatus: MutableLiveData<SeriesStatus?> = MutableLiveData()

    private val _selectedRating: MutableLiveData<Float?> = MutableLiveData()
    val selectedRating: LiveData<Float?> get() = _selectedRating

    val ratings: List<Float> = listOf(3.5f, 4.0f, 4.5f, 4.8f)

    private val _selectedKeywordIds: MutableLiveData<MutableList<Int>?> =
        MutableLiveData(mutableListOf())
    val selectedKeywordIds: LiveData<List<Int>?> get() = _selectedKeywordIds.map { it?.toList() }

    private val _appliedFiltersMessage: MediatorLiveData<String?> = MediatorLiveData()
    val appliedFiltersMessage: LiveData<String?> get() = _appliedFiltersMessage

    private val _isInfoChipSelected: MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isInfoChipSelected: LiveData<Boolean> get() = _isInfoChipSelected

    private val _isKeywordChipSelected: MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isKeywordChipSelected: LiveData<Boolean> get() = _isKeywordChipSelected

    private val _isNovelResultEmptyBoxVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNovelResultEmptyBoxVisibility: LiveData<Boolean> get() = _isNovelResultEmptyBoxVisibility

    private val _isBottomSheetOpen = MutableLiveData(false)

    init {
        _appliedFiltersMessage.apply {
            addSource(_selectedGenres) { updateMessage() }
            addSource(_isNovelCompleted) { updateMessage() }
            addSource(_selectedRating) { updateMessage() }
            addSource(_selectedKeywordIds) { updateMessage() }
        }

        _isInfoChipSelected.apply {
            addSource(_selectedGenres) { isInfoChipSelectedEnabled() }
            addSource(_isNovelCompleted) { isInfoChipSelectedEnabled() }
            addSource(_selectedRating) { isInfoChipSelectedEnabled() }
        }

        _isKeywordChipSelected.addSource(_selectedKeywordIds) { isKeywordChipSelectedEnabled() }
        updateKeyword(null)
    }

    private fun updateMessage() {
        if (_isBottomSheetOpen.value == true) return

        val appliedFilters = mutableListOf<String>()

        _selectedGenres.value?.let { genres ->
            if (genres.isNotEmpty()) {
                appliedFilters.add(GENRES_LABEL)
            }
        }

        _isNovelCompleted.value?.let {
            appliedFilters.add(NOVEL_COMPLETED_LABEL)
        }

        _selectedRating.value?.let {
            appliedFilters.add(RATING_LABEL)
        }

        _selectedKeywordIds.value?.let { keywords ->
            if (keywords.isNotEmpty()) {
                appliedFilters.add(KEYWORDS_LABEL)
            }
        }

        _appliedFiltersMessage.value = appliedFilters.joinToString(FILTER_SEPARATOR)
    }

    private fun isInfoChipSelectedEnabled() {
        val isGenreChipSelected: Boolean = _selectedGenres.value?.isNotEmpty() == true
        val isStatusChipSelected: Boolean = _isNovelCompleted.value != null
        val isRatingChipSelected: Boolean = _selectedRating.value != null

        _isInfoChipSelected.value =
            isGenreChipSelected || isStatusChipSelected || isRatingChipSelected
    }

    private fun isKeywordChipSelectedEnabled() {
        _isKeywordChipSelected.value = selectedKeywordIds.value?.isNotEmpty()
    }

    fun updatePreviousSearchFilteredValue(detailExploreFilteredModel: DetailExploreFilteredModel) {
        _selectedGenres.value = detailExploreFilteredModel.genres?.toMutableList()
        _isNovelCompleted.value = detailExploreFilteredModel.isCompleted
        _selectedRating.value = detailExploreFilteredModel.novelRating
        _selectedKeywordIds.value = detailExploreFilteredModel.keywordIds?.toMutableList()

        val currentUiState = _uiState.value ?: return
        val selectedKeywordIds = _selectedKeywordIds.value.orEmpty()

        val updatedCategories = currentUiState.categories.map { category ->
            val updatedKeywords = category.keywords.map { existingKeyword ->
                if (selectedKeywordIds.contains(existingKeyword.keywordId)) {
                    existingKeyword.copy(isSelected = !existingKeyword.isSelected)
                } else {
                    existingKeyword
                }
            }
            category.copy(keywords = updatedKeywords)
        }

        updateSearchResult(true)
        _uiState.value = currentUiState.copy(categories = updatedCategories)
    }


    fun updateSearchResult(isSearchButtonClick: Boolean) {
        if (uiState.value?.isLoadable == false && !isSearchButtonClick) return

        viewModelScope.launch {
            runCatching {
                getDetailExploreResultUseCase(
                    genres = selectedGenres.value?.map { it.titleEn },
                    isCompleted = isNovelCompleted.value,
                    novelRating = selectedRating.value,
                    keywordIds = selectedKeywordIds.value,
                    isSearchButtonClick = isSearchButtonClick,
                )
            }.onSuccess { results ->
                when (results.novels.isNotEmpty()) {
                    true -> {
                        _uiState.value = uiState.value?.copy(
                            loading = false,
                            isLoadable = results.isLoadable,
                            novels = results.novels.map { it.toUi() },
                            novelCount = results.resultCount,
                        )
                        _isNovelResultEmptyBoxVisibility.value = false
                    }

                    false -> {
                        _uiState.value = uiState.value?.copy(
                            loading = false,
                            isLoadable = results.isLoadable,
                            novelCount = results.resultCount,
                            novels = emptyList(),
                        )
                        _isNovelResultEmptyBoxVisibility.value = true
                    }
                }
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
                _isNovelResultEmptyBoxVisibility.value = false
            }
        }
    }

    fun updateSelectedInfoValueClear() {
        _selectedGenres.value = mutableListOf()
        _isNovelCompleted.value = null
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
        _isNovelCompleted.value = status?.isCompleted
    }

    fun updateSelectedRating(rating: Float?) {
        _selectedRating.value = rating
    }

    fun updateKeyword(searchWord: String?) {
        viewModelScope.launch {
            runCatching {
                keywordRepository.fetchKeywords(searchWord)
            }.onSuccess { keywordsList ->
                val categoriesModel =
                    CategoriesModel(categories = keywordsList.categories.map { it.toUi() })

                val selectedKeywordIds = selectedKeywordIds.value.orEmpty()

                val updatedCategories = categoriesModel.categories.map { category ->
                    val updatedKeywords = category.keywords.map { keyword ->
                        if (selectedKeywordIds.contains(keyword.keywordId)) {
                            keyword.copy(isSelected = true)
                        } else {
                            keyword
                        }
                    }
                    category.copy(keywords = updatedKeywords)
                }

                _uiState.value = when (searchWord) {
                    null -> {
                        uiState.value?.copy(
                            loading = false,
                            categories = updatedCategories,
                        )
                    }

                    else -> {
                        val results = updatedCategories.flatMap { it.keywords }
                        uiState.value?.copy(
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

        val selectedKeywordIds = updatedCategories.flatMap { category ->
            category.keywords.filter { it.isSelected }.map { it.keywordId }
        }

        val isAnyKeywordSelected = updatedCategories.any { category ->
            category.keywords.any { it.isSelected }
        }

        _selectedKeywordIds.value = selectedKeywordIds.toMutableList()
        _isKeywordChipSelected.value = isAnyKeywordSelected
        _uiState.value = currentUiState.copy(categories = updatedCategories)
    }

    fun updateSelectedKeywordValueClear() {
        val currentState = _uiState.value ?: return
        val updatedCategories = currentState.categories

        val resetCategories = updatedCategories.map { category ->
            category.copy(keywords = category.keywords.map { keyword ->
                keyword.copy(isSelected = false)
            })
        }

        _selectedKeywordIds.value = mutableListOf()
        _uiState.value = currentState.copy(categories = resetCategories)
        _isKeywordChipSelected.value = false
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

    fun updateIsBottomSheetOpen(isOpen: Boolean) {
        _isBottomSheetOpen.value = isOpen

        if (!isOpen) updateMessage()
    }

    companion object {
        private const val GENRES_LABEL = "장르"
        private const val NOVEL_COMPLETED_LABEL = "연재상태"
        private const val RATING_LABEL = "별점"
        private const val KEYWORDS_LABEL = "키워드"
        private const val FILTER_SEPARATOR = ", "
    }
}