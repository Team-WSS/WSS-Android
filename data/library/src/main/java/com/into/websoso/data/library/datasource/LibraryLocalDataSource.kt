package com.into.websoso.data.library.datasource

interface LibraryLocalDataSource {
    suspend fun getFullLibrary()
}
