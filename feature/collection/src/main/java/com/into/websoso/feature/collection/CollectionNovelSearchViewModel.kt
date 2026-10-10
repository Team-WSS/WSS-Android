package com.into.websoso.feature.collection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.into.websoso.data.novel.NovelSearchRepository
import com.into.websoso.data.novel.model.NovelSearchEntity
import com.into.websoso.domain.collection.CollectionRepository
import com.into.websoso.domain.collection.model.SaveCollection
import com.into.websoso.feature.collection.model.CollectionCreateUiState
import com.into.websoso.feature.collection.model.CollectionSelectedNovel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CollectionNovelSearchViewModel
    @Inject
    constructor(
        novelSearchRepository: NovelSearchRepository,
        private val collectionRepository: CollectionRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val collectionId: Long = savedStateHandle["collectionId"] ?: 0L
        private val _submittedQuery = MutableStateFlow("")
        val submittedQuery: StateFlow<String> = _submittedQuery.asStateFlow()

        private val _selectedNovels = MutableStateFlow<List<CollectionSelectedNovel>>(emptyList())
        val selectedNovels: StateFlow<List<CollectionSelectedNovel>> = _selectedNovels.asStateFlow()

        private val _representativeNovelId = MutableStateFlow<Long?>(null)
        val representativeNovelId: StateFlow<Long?> = _representativeNovelId.asStateFlow()

        private val _createUiState = MutableStateFlow(CollectionCreateUiState())
        val createUiState: StateFlow<CollectionCreateUiState> = _createUiState.asStateFlow()

        init {
            if (collectionId != 0L) loadCollection()
        }

        fun loadCollection() {
            if (_createUiState.value.isInitialLoading) return
            _createUiState.value = _createUiState.value.copy(isInitialLoading = true, isLoadError = false)
            viewModelScope.launch {
                try {
                    val collection = collectionRepository.getCollection(collectionId)
                    check(collection.isMine)
                    _selectedNovels.value = collection.novels.map {
                        CollectionSelectedNovel(it.id, it.title, it.author, it.imageUrl)
                    }
                    _representativeNovelId.value = collection.representativeNovelId
                    _createUiState.value = CollectionCreateUiState(initialCollection = collection)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _createUiState.value = CollectionCreateUiState(isLoadError = true)
                }
            }
        }

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
            if (_selectedNovels.value.size >= 100) return

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
            if (novels.size > 100 || novels.distinctBy { it.novelId }.size != novels.size) return
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

        fun createCollection(
            name: String,
            description: String,
            isPrivate: Boolean,
        ) {
            if (_createUiState.value.isLoading) return
            if (_createUiState.value.createdCollectionId != null) return
            if (_createUiState.value.isInitialLoading || _createUiState.value.isLoadError) return

            val representativeNovelId = _representativeNovelId.value ?: return
            val collection = runCatching {
                SaveCollection(
                    name = name,
                    description = description,
                    isPublic = !isPrivate,
                    novelIds = _selectedNovels.value.map(CollectionSelectedNovel::novelId),
                    representativeNovelId = representativeNovelId,
                )
            }.getOrElse {
                _createUiState.value = _createUiState.value.copy(isError = true)
                return
            }

            _createUiState.value = _createUiState.value.copy(isLoading = true, isError = false)
            viewModelScope.launch {
                try {
                    val savedId = if (collectionId == 0L) {
                        collectionRepository.createCollection(collection)
                    } else {
                        collectionRepository.updateCollection(collectionId, collection)
                        collectionId
                    }
                    _createUiState.value = _createUiState.value.copy(isLoading = false, createdCollectionId = savedId)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _createUiState.value = _createUiState.value.copy(isLoading = false, isError = true)
                }
            }
        }

        fun consumeCreateResult() {
            _createUiState.value = _createUiState.value.copy(createdCollectionId = null, isError = false)
        }
    }

private fun NovelSearchEntity.toSelectedNovel(): CollectionSelectedNovel =
    CollectionSelectedNovel(
        novelId = novelId,
        title = title,
        author = author,
        imageUrl = imageUrl,
    )
