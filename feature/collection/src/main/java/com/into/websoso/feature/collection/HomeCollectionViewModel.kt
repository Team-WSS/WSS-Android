package com.into.websoso.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.domain.collection.CollectionRepository
import com.into.websoso.domain.collection.model.PublicCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeCollectionViewModel
    @Inject
    constructor(
        private val repository: CollectionRepository,
    ) : ViewModel() {
        private val _collections = MutableStateFlow<List<PublicCollection>>(emptyList())
        val collections = _collections.asStateFlow()
        private var refreshJob: Job? = null

        fun refresh() {
            // 진행 중인 요청의 응답은 수정 전 데이터일 수 있으므로 취소하고 새로 조회한다.
            refreshJob?.cancel()
            refreshJob = viewModelScope.launch {
                try {
                    _collections.value = repository.getPublicCollectionPreview()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // 갱신에 실패하면 이전 목록을 유지한다. 처음부터 실패하면 목록이 비어 섹션이 숨겨진다.
                }
            }
        }
    }
