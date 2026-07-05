package com.into.websoso.feature.library

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.domain.library.model.AttractivePoint
import com.into.websoso.domain.library.model.AttractivePoints.Companion.toAttractivePoints
import com.into.websoso.domain.library.model.Genre
import com.into.websoso.domain.library.model.Genres.Companion.toGenres
import com.into.websoso.domain.library.model.Keyword
import com.into.websoso.domain.library.model.Keywords
import com.into.websoso.domain.library.model.RatingFilter
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.domain.library.model.ReadStatuses.Companion.toReadStatuses
import com.into.websoso.domain.library.model.SeriesStatus
import com.into.websoso.domain.library.model.SeriesStatuses
import com.into.websoso.domain.library.model.SortCriteria
import com.into.websoso.feature.library.mapper.toUiModel
import com.into.websoso.feature.library.model.LibraryFilterUiModel
import com.into.websoso.feature.library.model.LibraryUiState
import com.into.websoso.feature.library.model.NovelUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        private val libraryRepository: LibraryRepository,
        private val filterRepository: FilterRepository,
    ) : ViewModel() {
        private var pagingJob: Job? = null

        private val _uiState = MutableStateFlow(LibraryUiState())
        val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

        private val _tempFilterUiState = MutableStateFlow(uiState.value.libraryFilterUiModel)
        val tempFilterUiState = _tempFilterUiState.asStateFlow()

        private val _scrollToTopEvent = Channel<Unit>(Channel.BUFFERED)
        val scrollToTopEvent: Flow<Unit> = _scrollToTopEvent.receiveAsFlow()

        private val _keywordLimitEvent = Channel<Unit>(Channel.BUFFERED)
        val keywordLimitEvent: Flow<Unit> = _keywordLimitEvent.receiveAsFlow()

        val novels: MutableStateFlow<PagingData<NovelUiModel>> = MutableStateFlow(PagingData.empty())

        init {
            loadLibrary()
            loadNovelsCount()
            updateLibraryFilter()
            loadRegisteredKeywords()
        }

        private fun loadRegisteredKeywords() {
            viewModelScope.launch {
                runCatching { libraryRepository.getRegisteredKeywords() }
                    .onSuccess { entities ->
                        val registered =
                            entities.map { Keyword(id = it.keywordId, name = it.keywordName) }
                        _uiState.update { state ->
                            state.copy(
                                libraryFilterUiModel = state.libraryFilterUiModel.copy(
                                    keywords = state.libraryFilterUiModel.keywords.copy(registered = registered),
                                ),
                            )
                        }
                        _tempFilterUiState.update { temp ->
                            temp.copy(keywords = temp.keywords.copy(registered = registered))
                        }
                    }.onFailure { Log.e("LibraryViewModel", "등록 키워드 로드 실패", it) }
            }
        }

        private fun loadNovelsCount() {
            viewModelScope.launch {
                libraryRepository.novelTotalCount.collect { result ->
                    _uiState.update { it.copy(novelTotalCount = result) }
                }
            }
        }

        private fun loadLibrary() {
            pagingJob?.cancel()

            pagingJob = viewModelScope.launch {
                libraryRepository
                    .getLibraryFlow()
                    .map { pagingData -> pagingData.map(NovelEntity::toUiModel) }
                    .cachedIn(viewModelScope)
                    .collect { result ->
                        novels.update { result }
                    }
            }
        }

        fun refresh() {
            loadLibrary()
        }

        private fun updateLibraryFilter() {
            viewModelScope.launch {
                filterRepository.filterFlow.collect { filter ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            libraryFilterUiModel = uiState.libraryFilterUiModel.copy(
                                sortCriteria = SortCriteria.from(filter.sortCriteria),
                                isInterested = filter.isInterested,
                                readStatuses = filter.readStatuses.toReadStatuses(),
                                genres = filter.genres.toGenres(),
                                seriesStatuses = SeriesStatuses.fromIsComplete(filter.isComplete),
                                attractivePoints = filter.attractivePoints.toAttractivePoints(),
                                ratingFilter = RatingFilter(
                                    min = filter.ratingMin,
                                    max = filter.ratingMax,
                                    isRatingless = filter.isRatingless,
                                ),
                                keywords = uiState.libraryFilterUiModel.keywords.copy(
                                    selectedNames = filter.keywords.toSet(),
                                ),
                            ),
                        )
                    }
                }
            }
        }

        fun updateViewType() {
            _uiState.update {
                it.copy(isGrid = !it.isGrid)
            }
        }

        fun resetScrollPosition() {
            viewModelScope.launch {
                _scrollToTopEvent.send(Unit)
            }
        }

        fun updateSortCriteria(sortCriteria: SortCriteria) {
            viewModelScope.launch {
                filterRepository.updateFilter(
                    sortCriteria = sortCriteria.key,
                )
            }
        }

        fun updateInterestedNovels() {
            val updatedInterested = !uiState.value.libraryFilterUiModel.isInterested

            viewModelScope.launch {
                filterRepository.updateFilter(
                    isInterested = updatedInterested,
                )
            }
        }

        fun updateMyLibraryFilter() {
            _tempFilterUiState.update {
                uiState.value.libraryFilterUiModel
            }
        }

        fun updateReadStatus(readStatus: ReadStatus) {
            _tempFilterUiState.update {
                it.copy(readStatuses = it.readStatuses.set(readStatus))
            }
        }

        fun updateGenre(genre: Genre) {
            _tempFilterUiState.update {
                it.copy(genres = it.genres.set(genre))
            }
        }

        fun updateSeriesStatus(seriesStatus: SeriesStatus) {
            _tempFilterUiState.update {
                it.copy(seriesStatuses = it.seriesStatuses.set(seriesStatus))
            }
        }

        fun updateAttractivePoints(attractivePoint: AttractivePoint) {
            _tempFilterUiState.update {
                it.copy(attractivePoints = it.attractivePoints.set(attractivePoint))
            }
        }

        fun updateRatingRange(
            min: Float,
            max: Float,
        ) {
            _tempFilterUiState.update {
                it.copy(ratingFilter = it.ratingFilter.setRange(min, max))
            }
        }

        fun updateRatingless() {
            _tempFilterUiState.update {
                it.copy(ratingFilter = it.ratingFilter.toggleRatingless())
            }
        }

        fun clearRating() {
            _tempFilterUiState.update {
                it.copy(ratingFilter = RatingFilter())
            }
        }

        fun updateKeyword(keywordName: String) {
            val current = _tempFilterUiState.value.keywords
            val isNewSelection = !current.isSelected(keywordName)

            if (isNewSelection && current.selectedNames.size >= Keywords.MAX_SELECTION) {
                viewModelScope.launch { _keywordLimitEvent.send(Unit) }
                return
            }

            _tempFilterUiState.update {
                it.copy(keywords = it.keywords.set(keywordName))
            }
        }

        fun resetFilter() {
            _tempFilterUiState.update { current ->
                LibraryFilterUiModel(
                    isInterested = current.isInterested,
                    sortCriteria = current.sortCriteria,
                    keywords = Keywords(registered = current.keywords.registered),
                )
            }
        }

        fun searchFilteredNovels() {
            val tempFilter = _tempFilterUiState.value

            viewModelScope.launch {
                filterRepository.applyLibraryFilter(
                    readStatuses = tempFilter.readStatuses.selectedKeys,
                    attractivePoints = tempFilter.attractivePoints.selectedKeys,
                    genres = tempFilter.genres.selectedKeys,
                    isComplete = tempFilter.seriesStatuses.isComplete,
                    ratingMin = tempFilter.ratingFilter.min,
                    ratingMax = tempFilter.ratingFilter.max,
                    isRatingless = tempFilter.ratingFilter.isRatingless,
                    keywords = tempFilter.keywords.selectedLabels,
                )
            }
        }
    }
