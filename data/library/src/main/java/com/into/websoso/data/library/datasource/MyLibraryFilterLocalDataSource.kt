package com.into.websoso.data.library.datasource

import com.into.websoso.data.library.model.LibraryFilterParams
import kotlinx.coroutines.flow.Flow

interface MyLibraryFilterLocalDataSource {
    val myLibraryFilterFlow: Flow<LibraryFilterParams?>

    suspend fun updateMyLibraryFilter(
        readStatuses: Map<String, Boolean>,
        attractivePoints: Map<String, Boolean>,
        novelRating: Float,
    )

    suspend fun deleteMyLibraryFilter()
}
