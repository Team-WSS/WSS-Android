package com.into.websoso.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.into.websoso.core.database.WebsosoDatabase
import com.into.websoso.core.database.entity.InDatabaseFilteredNovelEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Dao
interface FilteredNovelDao {
    @Query("SELECT * FROM filtered_novels ORDER BY sortIndex ASC")
    fun selectAllNovels(): PagingSource<Int, InDatabaseFilteredNovelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilteredNovels(novels: List<InDatabaseFilteredNovelEntity>)

    @Query("DELETE FROM filtered_novels")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM filtered_novels")
    suspend fun selectNovelsCount(): Int
}

@Module
@InstallIn(SingletonComponent::class)
internal object FilteredNovelDaoModule {
    @Provides
    @Singleton
    internal fun provideFilteredNovelDao(database: WebsosoDatabase): FilteredNovelDao = database.filteredNovelDao()
}
