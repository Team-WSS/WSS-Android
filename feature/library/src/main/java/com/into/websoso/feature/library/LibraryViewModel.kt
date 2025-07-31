package com.into.websoso.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.domain.library.model.AttractivePoint
import com.into.websoso.domain.library.model.AttractivePoints.Companion.toAttractivePoints
import com.into.websoso.domain.library.model.NovelRating
import com.into.websoso.domain.library.model.Rating
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.domain.library.model.ReadStatuses.Companion.toReadStatuses
import com.into.websoso.domain.library.model.SortCriteria
import com.into.websoso.feature.library.model.LibraryFilterUiModel
import com.into.websoso.feature.library.model.LibraryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        libraryRepository: LibraryRepository,
        private val filterRepository: FilterRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LibraryUiState())
        val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

        private val _tempFilterUiState = MutableStateFlow(uiState.value.libraryFilterUiModel)
        val tempFilterUiState = _tempFilterUiState.asStateFlow()

        private val _scrollToTopEvent = Channel<Unit>(Channel.BUFFERED)
        val scrollToTopEvent: Flow<Unit> = _scrollToTopEvent.receiveAsFlow()

        val novels: Flow<PagingData<NovelEntity>> =
            libraryRepository.libraryFlow.cachedIn(viewModelScope)

        init {
            updateLibraryFilter()
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
                                attractivePoints = filter.attractivePoints.toAttractivePoints(),
                                novelRating = NovelRating.from(filter.novelRating),
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

        fun updateSortType() {
            val current = uiState.value.libraryFilterUiModel.sortCriteria
            val updatedSortCriteria = when (current) {
                SortCriteria.RECENT -> SortCriteria.OLD
                SortCriteria.OLD -> SortCriteria.RECENT
            }

            viewModelScope.launch {
                filterRepository.updateFilter(
                    sortCriteria = updatedSortCriteria.key,
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

        fun updateAttractivePoints(attractivePoint: AttractivePoint) {
            _tempFilterUiState.update {
                it.copy(attractivePoints = it.attractivePoints.set(attractivePoint))
            }
        }

        fun updateRating(rating: Rating) {
            _tempFilterUiState.update {
                it.copy(novelRating = it.novelRating.set(rating))
            }
        }

        fun resetFilter() {
            _tempFilterUiState.update {
                LibraryFilterUiModel()
            }
        }

        fun searchFilteredNovels() {
            viewModelScope.launch {
                filterRepository.updateFilter(
                    readStatuses = _tempFilterUiState.value.readStatuses.selectedKeys,
                    attractivePoints = _tempFilterUiState.value.attractivePoints.selectedKeys,
                    novelRating = _tempFilterUiState.value.novelRating.rating.value,
                )
            }
        }
    }
