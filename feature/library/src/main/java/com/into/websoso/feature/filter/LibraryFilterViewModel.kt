package com.into.websoso.feature.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.feature.library.model.LibraryFilterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryFilterViewModel
    @Inject
    constructor(
        private val libraryRepository: LibraryRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LibraryFilterUiState())
        val uiState = _uiState.asStateFlow()

        fun updateMyLibraryFilter(libraryFilterUiState: LibraryFilterUiState) {
            viewModelScope.launch {
                _uiState.update {
                    libraryFilterUiState
                }
            }
        }

        fun updateReadStatus(readStatus: ReadStatus) {
            _uiState.update {
                it.copy(
                    readStatuses = it.readStatuses.mapValues { (key, value) ->
                        if (key == readStatus) !value else value
                    },
                )
            }
        }

        fun updateAttractivePoints(attractivePoint: AttractivePoints) {
            _uiState.update {
                it.copy(
                    attractivePoints = it.attractivePoints.mapValues { (key, value) ->
                        if (key == attractivePoint) !value else value
                    },
                )
            }
        }

        fun updateRating(rating: Float) {
            _uiState.update {
                it.copy(
                    novelRating = if (it.novelRating == rating) 0f else rating,
                )
            }
        }

        fun resetFilter() {
            _uiState.update {
                LibraryFilterUiState()
            }
        }

        fun searchFilteredNovels() {
            viewModelScope.launch {
                libraryRepository.updateMyLibraryFilter(
                    readStatuses = uiState.value.readStatuses.mapKeys { it.key.name },
                    attractivePoints = uiState.value.attractivePoints.mapKeys { it.key.name },
                    novelRating = uiState.value.novelRating,
                )
            }
        }
    }
