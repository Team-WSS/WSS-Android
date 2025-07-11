package com.into.websoso.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.domain.library.GetFilteredNovelUseCase
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
        private val getFilteredNovelUseCase: GetFilteredNovelUseCase,
        private val libraryRepository: LibraryRepository,
    ) : ViewModel() {
        val novelPagingData: Flow<PagingData<NovelEntity>> =
            getUserNovelUseCase().cachedIn(viewModelScope)
    private val queryParams = MutableStateFlow(LibraryQueryParams())

        private val _uiState = MutableStateFlow(LibraryUiState())
        val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

        fun updateViewType() {
            _uiState.update {
                it.copy(isGrid = !it.isGrid)
            }
        }

        fun updateSortType() {
            val currentSortType = _uiState.value.selectedSortType.sortType
            val changedSortType = when (currentSortType) {
                SortType.RECENT -> SortType.OLD
                SortType.OLD -> SortType.RECENT
            }
            queryParams.update { it.copy(sortType = changedSortType) }
            _uiState.update { it.copy(selectedSortType = SortTypeUiModel.from(changedSortType)) }
        }

        fun updateInterestedNovels() {
            _uiState.update {
                it.copy(isInterested = !it.isInterested)
            }
        }
    }

data class LibraryQueryParams(
    val userId: Long = 184,
    val lastUserNovelId: Long = 0,
    val size: Int = 60,
    val sortType: SortType = SortType.RECENT,
    val isInterest: Boolean? = null,
    val readStatuses: List<String>? = null,
    val attractivePoints: List<String>? = null,
    val novelRating: Float? = null,
    val query: String? = null,
)
