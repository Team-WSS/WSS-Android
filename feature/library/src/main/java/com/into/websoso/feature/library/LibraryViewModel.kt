package com.into.websoso.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.into.websoso.data.library.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
//        private val libraryRepository: LibraryRepository,
    ) : ViewModel() {
//        val novels = libraryRepository.getUserLibrary().cachedIn(viewModelScope)
    }
