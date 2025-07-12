package com.into.websoso.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.domain.library.GetLibraryUseCase
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.domain.library.model.SortType
import com.into.websoso.feature.library.model.LibraryUiState
import com.into.websoso.feature.library.model.SortTypeUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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

        @OptIn(ExperimentalCoroutinesApi::class)
        val novelPagingData = uiState
            .map { it.libraryFilterUiState }
            .distinctUntilChanged()
            .flatMapLatest { filter ->
                getLibraryUseCase(
                    isInterested = uiState.value.isInterested,
                    readStatuses = filter.readStatuses,
                    attractivePoints = filter.attractivePoints,
                    novelRating = filter.novelRating,
                )
            }.cachedIn(viewModelScope)

        init {
            updateMyLibraryFilter()
        }

        private fun updateMyLibraryFilter() {
            viewModelScope.launch {
                libraryRepository.myLibraryFilter.collect { myFilter ->
                    if (myFilter != null) {
                        _uiState.update { uiState ->
                            uiState.copy(
                                isInterested = myFilter.isInterest ?: false,
                                libraryFilterUiState = uiState.libraryFilterUiState.copy(
                                    readStatuses = myFilter.readStatuses.mapKeys {
                                        ReadStatus.valueOf(it.key)
                                    },
                                    attractivePoints = myFilter.attractivePoints.mapKeys {
                                        AttractivePoints.valueOf(it.key)
                                    },
                                    novelRating = myFilter.novelRating ?: 0f,
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
            val current = _uiState.value.selectedSortType.sortType
            val newSortType = when (current) {
                SortType.RECENT -> SortType.OLD
                SortType.OLD -> SortType.RECENT
            }
            _uiState.update { it.copy(selectedSortType = SortTypeUiModel.from(newSortType)) }
        }

        fun updateInterestedNovels() {
            _uiState.update {
                it.copy(isInterested = !it.isInterested)
            }
        }
    }
