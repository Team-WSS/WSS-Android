package com.into.websoso.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.feature.collection.mapper.toUiModel
import com.into.websoso.feature.collection.model.CollectionLibraryNovelUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class CollectionLibraryNovelSelectionViewModel
    @Inject
    constructor(
        libraryRepository: LibraryRepository,
    ) : ViewModel() {
        private val _selectedNovelIds = MutableStateFlow<Set<Long>>(emptySet())
        val selectedNovelIds: StateFlow<Set<Long>> = _selectedNovelIds.asStateFlow()

        val novels: Flow<PagingData<CollectionLibraryNovelUiModel>> =
            libraryRepository
                .getUnfilteredLibraryFlow()
                .map { pagingData -> pagingData.map(NovelEntity::toUiModel) }
                .cachedIn(viewModelScope)

        fun toggleNovelSelection(novelId: Long) {
            _selectedNovelIds.update { selectedNovelIds ->
                if (novelId in selectedNovelIds) {
                    selectedNovelIds - novelId
                } else {
                    selectedNovelIds + novelId
                }
            }
        }

        fun setSelectedNovelIds(novelIds: Set<Long>) {
            _selectedNovelIds.value = novelIds
        }
    }
