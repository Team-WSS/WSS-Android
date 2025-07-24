package com.into.websoso.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.domain.library.GetLibraryUseCase
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.feature.library.model.LibraryUiState
import com.into.websoso.feature.library.model.SortTypeUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        getLibraryUseCase: GetLibraryUseCase,
        private val libraryRepository: LibraryRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LibraryUiState())
        val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

        private val _scrollToTopEvent = Channel<Unit>(Channel.BUFFERED)
        val scrollToTopEvent: Flow<Unit> = _scrollToTopEvent.receiveAsFlow()

        @OptIn(ExperimentalCoroutinesApi::class)
        val novelPagingData = uiState
            .map { it.libraryFilterUiState }
            .drop(INITIAL_STATE)
            .distinctUntilChanged()
            .flatMapLatest { filter ->
                getLibraryUseCase(
                    readStatuses = filter.readStatuses,
                    attractivePoints = filter.attractivePoints,
                    novelRating = filter.novelRating,
                    isInterested = filter.isInterested,
                    sortCriteria = filter.selectedSortType.name,
                )
            }.cachedIn(viewModelScope)

        init {
            updateMyLibraryFilter()
        }

        private fun updateMyLibraryFilter() {
            viewModelScope.launch {
                libraryRepository.myLibraryFilter.collectLatest { myFilter ->
                    if (myFilter != null) {
                        _uiState.update { uiState ->
                            uiState.copy(
                                libraryFilterUiState = uiState.libraryFilterUiState.copy(
                                    selectedSortType = SortTypeUiModel.valueOf(myFilter.sortCriteria),
                                    isInterested = myFilter.isInterested,
                                    readStatuses = myFilter.readStatuses
                                        .mapKeys {
                                            ReadStatus.valueOf(it.key)
                                        }.ifEmpty { uiState.libraryFilterUiState.readStatuses },
                                    attractivePoints = myFilter.attractivePoints
                                        .mapKeys {
                                            AttractivePoints.valueOf(it.key)
                                        }.ifEmpty { uiState.libraryFilterUiState.attractivePoints },
                                    novelRating = myFilter.novelRating,
                                ),
                            )
                        }
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
                libraryRepository.updateMyLibraryFilter(
                    sortCriteria = newSortType.name,
                )
            }

            _uiState.update { uiState ->
                uiState.copy(
                    libraryFilterUiState = uiState.libraryFilterUiState.copy(
                        selectedSortType = newSortType,
                    ),
                )
            }
        }

        fun updateInterestedNovels() {
            val updatedInterested = !uiState.value.libraryFilterUiState.isInterested

            viewModelScope.launch {
                libraryRepository.updateMyLibraryFilter(
                    isInterested = updatedInterested,
                )
            }

            _uiState.update {
                it.copy(
                    libraryFilterUiState = uiState.value.libraryFilterUiState.copy(
                        isInterested = updatedInterested,
                    ),
                )
            }
        }

        fun resetScrollPosition() {
            viewModelScope.launch {
                _scrollToTopEvent.send(Unit)
            }
        }

        companion object {
            private const val INITIAL_STATE = 1
        }
    }
