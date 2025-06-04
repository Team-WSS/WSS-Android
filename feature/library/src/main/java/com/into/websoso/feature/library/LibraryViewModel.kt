package com.into.websoso.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.into.websoso.domain.library.GetUserNovelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        getUserNovelUseCase: GetUserNovelUseCase,
    ) : ViewModel() {
        val novelList = getUserNovelUseCase().cachedIn(viewModelScope)
    }
