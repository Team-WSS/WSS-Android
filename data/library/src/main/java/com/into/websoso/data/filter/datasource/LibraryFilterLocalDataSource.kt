package com.into.websoso.data.filter.datasource

import com.into.websoso.data.filter.model.LibraryFilter
import kotlinx.coroutines.flow.Flow

interface LibraryFilterLocalDataSource {
    val libraryFilterFlow: Flow<LibraryFilter?>

    suspend fun updateLibraryFilter(params: LibraryFilter)

    suspend fun deleteLibraryFilter()
}
