package com.into.websoso.feature.collection.model

import com.into.websoso.domain.collection.model.CollectionDetail

internal data class CollectionCreateUiState(
    val isLoading: Boolean = false,
    val createdCollectionId: Long? = null,
    val isError: Boolean = false,
    val initialCollection: CollectionDetail? = null,
    val isInitialLoading: Boolean = false,
    val isLoadError: Boolean = false,
)
