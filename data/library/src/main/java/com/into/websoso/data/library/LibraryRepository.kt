package com.into.websoso.data.library

import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import javax.inject.Inject

class LibraryRepository
    @Inject
    constructor(
        private val libraryRemoteDataSource: LibraryRemoteDataSource,
        private val libraryLocalDataSource: LibraryLocalDataSource,
    ) {
        fun fetchLibrary() {
        }
    }
