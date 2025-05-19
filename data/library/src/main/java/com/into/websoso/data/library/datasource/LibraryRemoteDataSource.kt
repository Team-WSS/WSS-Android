package com.into.websoso.data.library.datasource

interface LibraryRemoteDataSource {
    suspend fun getFullLibrary()
}
