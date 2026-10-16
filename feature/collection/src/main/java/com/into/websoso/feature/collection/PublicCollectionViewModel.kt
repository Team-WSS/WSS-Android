package com.into.websoso.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.into.websoso.domain.collection.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PublicCollectionViewModel
    @Inject
    constructor(
        repository: CollectionRepository,
    ) : ViewModel() {
        val collections = repository.getPublicCollections().cachedIn(viewModelScope)
    }
