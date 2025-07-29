package com.into.websoso.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.feature.library.model.LibraryUiState
import com.into.websoso.feature.library.model.SortTypeUiModel
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
                            libraryFilterUiState = uiState.libraryFilterUiState.copy(
                                selectedSortType = SortTypeUiModel.valueOf(filter.sortCriteria),
                                isInterested = filter.isInterested,
                                readStatuses = filter.readStatuses
                                    .mapKeys {
                                        ReadStatus.from(it.key)
                                    }.ifEmpty { uiState.libraryFilterUiState.readStatuses },
                                attractivePoints = filter.attractivePoints
                                    .mapKeys {
                                        AttractivePoints.from(it.key)
                                    }.ifEmpty { uiState.libraryFilterUiState.attractivePoints },
                                novelRating = filter.novelRating,
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

        fun updateSortType() {
            val current = uiState.value.libraryFilterUiState.selectedSortType
            val newSortType = when (current) {
                SortTypeUiModel.RECENT -> SortTypeUiModel.OLD
                SortTypeUiModel.OLD -> SortTypeUiModel.RECENT
            }

            viewModelScope.launch {
                filterRepository.updateFilter(
                    sortCriteria = newSortType.name,
                )
            }
        }

        fun updateInterestedNovels() {
            val updatedInterested = !uiState.value.libraryFilterUiState.isInterested

            viewModelScope.launch {
                filterRepository.updateFilter(
                    isInterested = updatedInterested,
                )
            }
        }

        fun resetScrollPosition() {
            viewModelScope.launch {
                _scrollToTopEvent.send(Unit)
            }
        }
    }
