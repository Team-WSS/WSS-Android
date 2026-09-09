package com.into.websoso.feature.collection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.core.resource.R
import com.into.websoso.domain.collection.CollectionRepository
import com.into.websoso.domain.collection.model.CollectionDetail
import com.into.websoso.domain.collection.model.CollectionSortCriteria
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal data class CollectionDetailUiState(
    val collection: CollectionDetail? = null,
    val isLoading: Boolean = false,
    val isBusy: Boolean = false,
    val isDeleted: Boolean = false,
    val error: Int? = null,
    val sort: CollectionSortCriteria = CollectionSortCriteria.RECENT,
) {
    val showInitialError: Boolean
        get() = collection == null && !isLoading && error != null
}

@HiltViewModel
internal class CollectionDetailViewModel
    @Inject
    constructor(
        private val repository: CollectionRepository,
        private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val collectionId: Long = checkNotNull(savedStateHandle["collectionId"])
        private val _uiState = MutableStateFlow(
            CollectionDetailUiState(
                sort = CollectionSortCriteria.valueOf(savedStateHandle["sort"] ?: "RECENT"),
            ),
        )
        val uiState = _uiState.asStateFlow()

        fun refresh() {
            if (_uiState.value.isLoading || _uiState.value.isBusy || _uiState.value.isDeleted) return
            _uiState.update { it.copy(isLoading = true, error = null) }
            viewModelScope.launch {
                try {
                    val collection = repository.getCollection(collectionId, sortCriteria = _uiState.value.sort)
                    _uiState.update { it.copy(collection = collection, isLoading = false) }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _uiState.update { it.copy(isLoading = false, error = R.string.collection_load_failed) }
                }
            }
        }

        fun sort(criteria: CollectionSortCriteria) {
            val current = _uiState.value
            if (current.isLoading || current.isBusy || current.isDeleted || current.sort == criteria) return
            _uiState.update { it.copy(isLoading = true, error = null) }
            viewModelScope.launch {
                try {
                    val collection = repository.getCollection(collectionId, sortCriteria = criteria)
                    // sort와 collection을 한 번에 갱신해 "정렬 표시는 바뀌었는데 목록은 그대로"인
                    // 상태가 보이지 않게 한다. 실패하면 아래 catch에서 아무 것도 안 바꾸고 에러만 띄운다.
                    savedStateHandle["sort"] = criteria.name
                    _uiState.update { it.copy(collection = collection, sort = criteria, isLoading = false) }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _uiState.update { it.copy(isLoading = false, error = R.string.collection_sort_failed) }
                }
            }
        }

        fun toggleLike() {
            val current = _uiState.value
            val collection = current.collection ?: return
            if (current.isBusy || current.isLoading || current.isDeleted) return
            _uiState.update { it.copy(isBusy = true, error = null) }
            viewModelScope.launch {
                try {
                    repository.updateLike(collectionId, !collection.isLiked)
                    _uiState.update {
                        it.copy(
                            isBusy = false,
                            collection = collection.copy(
                                isLiked = !collection.isLiked,
                                likeCount = (collection.likeCount + if (collection.isLiked) -1 else 1).coerceAtLeast(0),
                            ),
                        )
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _uiState.update { it.copy(isBusy = false, error = R.string.collection_like_failed) }
                }
            }
        }

        fun delete() {
            val state = _uiState.value
            if (state.collection?.isMine != true || state.isBusy || state.isLoading || state.isDeleted) return
            _uiState.update { it.copy(isBusy = true, error = null) }
            viewModelScope.launch {
                try {
                    repository.deleteCollection(collectionId)
                    _uiState.update { it.copy(isBusy = false, isDeleted = true) }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _uiState.update { it.copy(isBusy = false, error = R.string.collection_delete_failed) }
                }
            }
        }

        fun consumeError() {
            _uiState.update { it.copy(error = null) }
        }
    }
