package com.into.websoso.domain.library

import com.into.websoso.data.library.LibraryRepository
import javax.inject.Inject

class GetUserNovelUseCase
    @Inject
    constructor(
        private val libraryRepository: LibraryRepository,
    )
