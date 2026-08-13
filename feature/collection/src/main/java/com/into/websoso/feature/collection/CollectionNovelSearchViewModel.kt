package com.into.websoso.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.into.websoso.data.novel.NovelSearchRepository
import com.into.websoso.data.novel.model.NovelSearchEntity
import com.into.websoso.feature.collection.model.CollectionSelectedNovel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class CollectionNovelSearchViewModel
    @Inject
    constructor(
        novelSearchRepository: NovelSearchRepository,
    ) : ViewModel() {
        private val _submittedQuery = MutableStateFlow("")
        val submittedQuery: StateFlow<String> = _submittedQuery.asStateFlow()

        private val _selectedNovels = MutableStateFlow<List<CollectionSelectedNovel>>(emptyList())
        val selectedNovels: StateFlow<List<CollectionSelectedNovel>> = _selectedNovels.asStateFlow()

        @OptIn(ExperimentalCoroutinesApi::class)
        val searchResults: Flow<PagingData<NovelSearchEntity>> =
            submittedQuery
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(PagingData.empty())
                    } else {
                        novelSearchRepository.searchNovels(query)
                    }
                }.cachedIn(viewModelScope)

        fun search(query: String) {
            _submittedQuery.value = query.trim()
        }

        fun addNovel(novel: NovelSearchEntity) {
            _selectedNovels.update { selectedNovels ->
                if (selectedNovels.any { it.novelId == novel.novelId }) {
                    selectedNovels
                } else {
                    selectedNovels + novel.toSelectedNovel()
                }
            }
        }

        fun removeNovel(novelId: Long) {
            _selectedNovels.update { selectedNovels ->
                selectedNovels.filterNot { it.novelId == novelId }
            }
        }

        fun updateSelectedNovels(novels: List<CollectionSelectedNovel>) {
            _selectedNovels.value = novels
        }
    }

private fun NovelSearchEntity.toSelectedNovel(): CollectionSelectedNovel =
    CollectionSelectedNovel(
        novelId = novelId,
        title = title,
        author = author,
        imageUrl = imageUrl,
    )
