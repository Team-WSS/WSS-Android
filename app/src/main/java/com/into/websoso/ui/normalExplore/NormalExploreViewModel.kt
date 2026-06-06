package com.into.websoso.ui.normalExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.model.SosoPickEntity
import com.into.websoso.data.repository.NovelRepository
import com.into.websoso.domain.usecase.GetNormalExploreResultUseCase
import com.into.websoso.ui.mapper.toUi
import com.into.websoso.ui.normalExplore.NormalExploreActivity.Companion.SEARCH_AUTHOR
import com.into.websoso.ui.normalExplore.model.NormalExploreModel.RecentSearchModel
import com.into.websoso.ui.normalExplore.model.NormalExploreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NormalExploreViewModel
    @Inject
    constructor(
        private val getNormalExploreResultUseCase: GetNormalExploreResultUseCase,
        private val novelRepository: NovelRepository,
        private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _uiState: MutableLiveData<NormalExploreUiState> =
            MutableLiveData(NormalExploreUiState())
        val uiState: LiveData<NormalExploreUiState> get() = _uiState

        private val initialSearchWord: String =
            savedStateHandle.get<String>(SEARCH_AUTHOR).orEmpty()

        private val _searchWord = MutableLiveData(initialSearchWord)
        val searchWord: MutableLiveData<String> get() = _searchWord

        private val _isSearchCancelButtonVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
        val isSearchCancelButtonVisibility: LiveData<Boolean> get() = _isSearchCancelButtonVisibility

        private val _isNovelResultEmptyBoxVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
        val isNovelResultEmptyBoxVisibility: LiveData<Boolean> get() = _isNovelResultEmptyBoxVisibility

        private val _sosoPicks: MutableLiveData<List<SosoPickEntity.NovelEntity>> =
            MutableLiveData(emptyList())
        val sosoPicks: LiveData<List<SosoPickEntity.NovelEntity>> get() = _sosoPicks

        private val _isSosoPickVisible: MutableLiveData<Boolean> =
            MutableLiveData(initialSearchWord.isBlank())
        val isSosoPickVisible: LiveData<Boolean> get() = _isSosoPickVisible

        private val _recentSearches: MutableLiveData<List<RecentSearchModel>> =
            MutableLiveData(emptyList())
        val recentSearches: LiveData<List<RecentSearchModel>> get() = _recentSearches

        private val _isRecentSearchesVisible: MutableLiveData<Boolean> = MutableLiveData(false)
        val isRecentSearchesVisible: LiveData<Boolean> get() = _isRecentSearchesVisible

        init {
            fetchSosoPicks()
            fetchRecentSearches()
            if (initialSearchWord.isNotBlank()) {
                updateSearchResult(isSearchButtonClick = true)
            }
        }

        private fun fetchSosoPicks() {
            viewModelScope.launch {
                runCatching {
                    novelRepository.fetchSosoPicks()
                }.onSuccess { result ->
                    _sosoPicks.value = result.novels
                }
            }
        }

        private fun fetchRecentSearches() {
            viewModelScope.launch {
                fetchRecentSearchesInternal()
            }
        }

        private suspend fun fetchRecentSearchesInternal() {
            runCatching {
                novelRepository.fetchRecentSearches()
            }.onSuccess { result ->
                _recentSearches.value = result.recentSearches
                    .map { it.toUi() }
                    .take(MAX_RECENT_SEARCH_COUNT)
                updateRecentSearchesVisibility()
            }
        }

        fun updateSearchWord(searchWord: String) {
            _searchWord.value = searchWord
            savedStateHandle[SEARCH_AUTHOR] = searchWord
        }

        fun updateSearchResult(isSearchButtonClick: Boolean) {
            if ((_searchWord.value.isNullOrBlank() || _uiState.value?.isLoadable == false) && !isSearchButtonClick) {
                return
            }
            if (isSearchButtonClick) {
                _isSosoPickVisible.value = false
                updateRecentSearchesVisibility()
            }
            viewModelScope.launch {
                _uiState.value = _uiState.value?.copy(loading = isSearchButtonClick)
                runCatching {
                    getNormalExploreResultUseCase(searchWord.value ?: "", isSearchButtonClick)
                }.onSuccess { results ->
                    if (results.novels.isNotEmpty()) {
                        _uiState.value = _uiState.value?.copy(
                            loading = false,
                            isLoadable = results.isLoadable,
                            novelCount = results.resultCount,
                            novels = results.novels.map { it.toUi() },
                        )
                        _isNovelResultEmptyBoxVisibility.value = false
                    } else {
                        _uiState.value = _uiState.value?.copy(
                            loading = false,
                            isLoadable = results.isLoadable,
                            novelCount = results.resultCount,
                            novels = emptyList(),
                        )
                        _isNovelResultEmptyBoxVisibility.value = true
                    }
                    if (isSearchButtonClick && searchWord.value.isNullOrBlank().not()) {
                        fetchRecentSearchesInternal()
                    }
                }.onFailure {
                    _uiState.value = _uiState.value?.copy(
                        loading = false,
                        error = true,
                    )
                    _isNovelResultEmptyBoxVisibility.value = false
                }
            }
        }

        fun validateSearchWordClearButton() {
            _isSearchCancelButtonVisibility.value = _searchWord.value.isNullOrEmpty().not()
        }

        fun updateSearchWordEmpty() {
            _searchWord.value = ""
            savedStateHandle[SEARCH_AUTHOR] = ""
            _uiState.value = NormalExploreUiState()
            _isNovelResultEmptyBoxVisibility.value = false
            _isSosoPickVisible.value = true
            updateRecentSearchesVisibility()
        }

        fun deleteRecentSearch(recentSearchId: Long) {
            viewModelScope.launch {
                runCatching {
                    novelRepository.deleteRecentSearch(recentSearchId)
                }.onSuccess {
                    _recentSearches.value = _recentSearches.value
                        .orEmpty()
                        .filterNot { recentSearch -> recentSearch.id == recentSearchId }
                    updateRecentSearchesVisibility()
                }
            }
        }

        fun deleteAllRecentSearches() {
            viewModelScope.launch {
                runCatching {
                    novelRepository.deleteAllRecentSearches()
                }.onSuccess {
                    _recentSearches.value = emptyList()
                    updateRecentSearchesVisibility()
                }
            }
        }

        private fun updateRecentSearchesVisibility() {
            _isRecentSearchesVisible.value =
                _isSosoPickVisible.value == true && _recentSearches.value.orEmpty().isNotEmpty()
        }

        companion object {
            private const val MAX_RECENT_SEARCH_COUNT = 30
        }
    }
