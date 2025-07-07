package com.into.websoso.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.into.websoso.core.database.WebsosoDatabase
import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Dao
interface NovelDao {
    @Query("SELECT * FROM novels ORDER BY userNovelId DESC")
    fun selectAllNovels(): PagingSource<Int, InDatabaseNovelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovels(novels: List<InDatabaseNovelEntity>)

    @Query("DELETE FROM novels")
    suspend fun clearAll()
}

@Module
@InstallIn(SingletonComponent::class)
internal object NovelDaoModule {
    @Provides
    @Singleton
    internal fun provideNovelDao(database: WebsosoDatabase): NovelDao = database.novelDao()
}
