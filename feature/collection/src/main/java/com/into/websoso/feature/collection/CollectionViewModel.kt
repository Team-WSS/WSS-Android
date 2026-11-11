package com.into.websoso.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.into.websoso.domain.collection.CollectionRepository
import com.into.websoso.domain.collection.model.Collection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class CollectionViewModel
    @Inject
    constructor(
        private val repository: CollectionRepository,
    ) : ViewModel() {
        val myCollections = repository.getMyCollections().cachedIn(viewModelScope)
        val likedCollections = repository.getLikedCollections().cachedIn(viewModelScope)
        private val userCollections = mutableMapOf<Long, Flow<PagingData<Collection>>>()

        fun collections(userId: Long?) =
            if (userId == null) {
                myCollections
            } else {
                userCollections.getOrPut(userId) {
                    repository.getUserCollections(userId).cachedIn(viewModelScope)
                }
            }
    }
