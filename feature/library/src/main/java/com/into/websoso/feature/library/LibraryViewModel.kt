package com.into.websoso.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.domain.library.GetFilteredNovelUseCase
import com.into.websoso.domain.library.GetUserNovelUseCase
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.AttractivePoints.CHARACTER
import com.into.websoso.domain.library.model.AttractivePoints.MATERIAL
import com.into.websoso.domain.library.model.AttractivePoints.RELATIONSHIP
import com.into.websoso.domain.library.model.AttractivePoints.VIBE
import com.into.websoso.domain.library.model.AttractivePoints.WORLDVIEW
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.domain.library.model.ReadStatus.QUIT
import com.into.websoso.domain.library.model.ReadStatus.WATCHED
import com.into.websoso.domain.library.model.ReadStatus.WATCHING
import com.into.websoso.domain.library.model.SortType
import com.into.websoso.feature.library.model.LibraryUiState
import com.into.websoso.feature.library.model.SortTypeUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
@Inject
constructor(
    getUserNovelUseCase: GetUserNovelUseCase,
    private val getFilteredNovelUseCase: GetFilteredNovelUseCase,
) : ViewModel() {
    val novelPagingData: Flow<PagingData<NovelEntity>> =
        getUserNovelUseCase().cachedIn(viewModelScope)

    private val _filterUiState = MutableStateFlow(FilterUiState())
    val filterUiState = _filterUiState.asStateFlow()

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
        _uiState.update { it.copy(selectedSortType = SortTypeUiModel.from(newSortType)) }
    }

    fun updateReadStatus(readStatus: ReadStatus) {
        _filterUiState.update {
            val updated = it.readStatuses.mapValues { (key, value) ->
                if (key == readStatus) !value else value
            }
            it.copy(readStatuses = updated)
        }
    }

    fun updateAttractivePoints(attractivePoint: AttractivePoints) {
        _filterUiState.update {
            val updated = it.attractivePoints.mapValues { (key, value) ->
                if (key == attractivePoint) !value else value
            }
            it.copy(attractivePoints = updated)
        }
    }

    fun updateRating(rating: Float) {
        _filterUiState.update {
            it.copy(novelRating = rating)
        }
    }

    fun resetFilter() {
        _filterUiState.update {
            FilterUiState()
        }
    }

        fun searchFilteredNovels() {
            getFilteredNovelUseCase(
                userId = 0L,
                lastUserNovelId = 0L,
                size = 0,
                sortCriteria = uiState.value.selectedSortType.sortType,
                isInterest = uiState.value.filterUiState.isInterested,
                readStatuses = filterUiState.value.readStatuses
                    .filterValues { it }
                    .keys,
                attractivePoints = filterUiState.value.attractivePoints
                    .filterValues { it }
                .keys,
            novelRating = filterUiState.value.novelRating,
        )
    }
}

data class FilterUiState(
    val readStatuses: Map<ReadStatus, Boolean> = mapOf(
        WATCHING to false,
        WATCHED to false,
        QUIT to false,
    ),
    val attractivePoints: Map<AttractivePoints, Boolean> = mapOf(
        VIBE to false,
        WORLDVIEW to false,
        CHARACTER to false,
        MATERIAL to false,
        RELATIONSHIP to false,
    ),
    val novelRating: Float = 0f,
)
