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

        private val _representativeNovelId = MutableStateFlow<Long?>(null)
        val representativeNovelId: StateFlow<Long?> = _representativeNovelId.asStateFlow()

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
            if (_selectedNovels.value.any { it.novelId == novel.novelId }) return

            _selectedNovels.value = listOf(novel.toSelectedNovel()) + _selectedNovels.value
            _representativeNovelId.value = novel.novelId
        }

        fun removeNovel(novelId: Long) {
            val remainingNovels = _selectedNovels.value.filterNot { it.novelId == novelId }
            _selectedNovels.value = remainingNovels

            if (_representativeNovelId.value == novelId) {
                _representativeNovelId.value = remainingNovels.firstOrNull()?.novelId
            }
        }

        fun updateSelectedNovels(novels: List<CollectionSelectedNovel>) {
            val previousNovelIds = _selectedNovels.value.map(CollectionSelectedNovel::novelId)
            val novelIds = novels.map(CollectionSelectedNovel::novelId)
            val selectedNovelIds = novelIds.toSet()
            val onlyRemovedNovels = novelIds == previousNovelIds.filter { it in selectedNovelIds }
            _selectedNovels.value = novels

            if (!onlyRemovedNovels || _representativeNovelId.value !in selectedNovelIds) {
                _representativeNovelId.value = novelIds.firstOrNull()
            }
        }

        fun updateRepresentativeNovel(novelId: Long) {
            if (_selectedNovels.value.any { it.novelId == novelId }) {
                _representativeNovelId.value = novelId
            }
        }
    }

private fun NovelSearchEntity.toSelectedNovel(): CollectionSelectedNovel =
    CollectionSelectedNovel(
        novelId = novelId,
        title = title,
        author = author,
        imageUrl = imageUrl,
    )
