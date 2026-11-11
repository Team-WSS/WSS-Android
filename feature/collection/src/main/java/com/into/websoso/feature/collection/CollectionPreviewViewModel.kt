package com.into.websoso.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.domain.collection.CollectionRepository
import com.into.websoso.domain.collection.model.CollectionPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CollectionPreviewViewModel
    @Inject
    constructor(
        private val repository: CollectionRepository,
    ) : ViewModel() {
        private val _page = MutableStateFlow<CollectionPage?>(null)
        val page = _page.asStateFlow()
        private val _isLoading = MutableStateFlow(false)
        val isLoading = _isLoading.asStateFlow()
        private val _isError = MutableStateFlow(false)
        val isError = _isError.asStateFlow()

        fun refresh(userId: Long?) {
            if (_isLoading.value) return
            _isLoading.value = true
            _isError.value = false
            viewModelScope.launch {
                try {
                    _page.value = if (userId == null) repository.getMyCollectionPreview() else repository.getUserCollectionPreview(userId)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _isError.value = true
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
