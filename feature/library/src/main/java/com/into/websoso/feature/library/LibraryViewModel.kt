package com.into.websoso.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.domain.library.GetUserNovelUseCase
import com.into.websoso.domain.library.model.SortType
import com.into.websoso.feature.library.model.LibraryUiState
import com.into.websoso.feature.library.model.SortTypeUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        private val getUserNovelUseCase: GetUserNovelUseCase,
    ) : ViewModel() {
        private val queryParams = MutableStateFlow(LibraryQueryParams())

        @OptIn(ExperimentalCoroutinesApi::class)
        val novelPagingData: Flow<PagingData<NovelEntity>> =
            queryParams
                .flatMapLatest { params ->
                    getUserNovelUseCase(
                        userId = params.userId,
                        lastUserNovelId = params.lastUserNovelId,
                        size = params.size,
                        sortCriteria = params.sortType,
                        isInterest = params.isInterest,
                        readStatuses = params.readStatuses,
                        attractivePoints = params.attractivePoints,
                        novelRating = params.novelRating,
                        query = params.query,
                    )
                }.cachedIn(viewModelScope)

        private val _uiState = MutableStateFlow(LibraryUiState())
        val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

        fun updateViewType() {
            _uiState.update {
                it.copy(isGrid = !it.isGrid)
            }
        }

        fun updateSortType(selected: SortTypeUiModel) {
            val newSortType = when (selected.sortType) {
                SortType.RECENT -> SortType.OLD
                SortType.OLD -> SortType.RECENT
            }
            queryParams.update { it.copy(sortType = newSortType) }
            _uiState.update { it.copy(selectedSortType = SortTypeUiModel.from(newSortType)) }
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
