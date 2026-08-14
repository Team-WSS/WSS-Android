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
import com.into.websoso.feature.collection.model.CollectionSelectedNovel
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
        private val _selectedNovels = MutableStateFlow<List<CollectionSelectedNovel>>(emptyList())
        val selectedNovels: StateFlow<List<CollectionSelectedNovel>> = _selectedNovels.asStateFlow()

        val novels: Flow<PagingData<CollectionLibraryNovelUiModel>> =
            libraryRepository
                .getUnfilteredLibraryFlow()
                .map { pagingData -> pagingData.map(NovelEntity::toUiModel) }
                .cachedIn(viewModelScope)

        fun toggleNovelSelection(novel: CollectionLibraryNovelUiModel) {
            _selectedNovels.update { selectedNovels ->
                if (selectedNovels.any { it.novelId == novel.novelId }) {
                    selectedNovels.filterNot { it.novelId == novel.novelId }
                } else {
                    selectedNovels + novel.toSelectedNovel()
                }
            }
        }

        fun setSelectedNovels(novels: List<CollectionSelectedNovel>) {
            _selectedNovels.value = novels
        }
    }

private fun CollectionLibraryNovelUiModel.toSelectedNovel(): CollectionSelectedNovel =
    CollectionSelectedNovel(
        novelId = novelId,
        title = title,
        author = "",
        imageUrl = imageUrl,
    )
