package com.into.websoso.feature.library

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.domain.library.GetUserNovelUseCase
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.domain.library.model.SortType
import com.into.websoso.feature.library.model.LibraryUiState
import com.into.websoso.feature.library.model.SortTypeUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        getUserNovelUseCase: GetUserNovelUseCase,
//    private val getFilteredNovelUseCase: GetFilteredNovelUseCase,
        private val libraryRepository: LibraryRepository,
    ) : ViewModel() {
        val novelPagingData: Flow<PagingData<NovelEntity>> =
            getUserNovelUseCase().cachedIn(viewModelScope)

        private val _uiState = MutableStateFlow(LibraryUiState())
        val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

        var listState by mutableStateOf(LazyListState())
            private set

        var gridState by mutableStateOf(LazyGridState())
            private set

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
                                    readStatuses = uiState.libraryFilterUiState.readStatuses +
                                        (
                                            myFilter.readStatuses?.map {
                                                ReadStatus.valueOf(it) to true
                                            } ?: emptyList()
                                        ),
                                    attractivePoints = uiState.libraryFilterUiState.attractivePoints +
                                        (
                                            myFilter.attractivePoints?.map {
                                                AttractivePoints.valueOf(it) to true
                                            } ?: emptyList()
                                        ),
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

        fun updateSortType(selected: SortTypeUiModel) {
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

        fun scrollToTop() {
            viewModelScope.launch {
                if (uiState.value.isGrid) {
                    gridState.scrollToItem(0)
                } else {
                    listState.scrollToItem(0)
                }
            }
        }
    }
