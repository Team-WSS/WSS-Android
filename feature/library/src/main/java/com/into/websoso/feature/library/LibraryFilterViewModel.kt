package com.into.websoso.feature.library

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
        private val _libraryFilterUiState = MutableStateFlow(LibraryFilterUiState())
        val libraryFilterUiState = _libraryFilterUiState.asStateFlow()

        fun updateMyLibraryFilter(libraryFilterUiState: LibraryFilterUiState) {
            viewModelScope.launch {
                _libraryFilterUiState.update {
                    libraryFilterUiState
                }
            }
        }

        fun updateReadStatus(readStatus: ReadStatus) {
            _libraryFilterUiState.update {
                it.copy(
                    readStatuses = it.readStatuses.mapValues { (key, value) ->
                        if (key == readStatus) !value else value
                    },
                )
            }
        }

        fun updateAttractivePoints(attractivePoint: AttractivePoints) {
            _libraryFilterUiState.update {
                it.copy(
                    attractivePoints = it.attractivePoints.mapValues { (key, value) ->
                        if (key == attractivePoint) !value else value
                    },
                )
            }
        }

        fun updateRating(rating: Float) {
            _libraryFilterUiState.update {
                it.copy(
                    novelRating = if (it.novelRating == rating) 0f else rating,
                )
            }
        }

        fun resetFilter() {
            _libraryFilterUiState.update {
                LibraryFilterUiState()
            }
        }

        fun searchFilteredNovels() {
            viewModelScope.launch {
                libraryRepository.updateMyLibraryFilter(
                    readStatuses = libraryFilterUiState.value.readStatuses
                        .filterValues { it }
                        .map { it.key.name },
                    attractivePoints = libraryFilterUiState.value.attractivePoints
                        .filterValues { it }
                        .map { it.key.name },
                    novelRating = libraryFilterUiState.value.novelRating,
                )
            }
        }
    }
