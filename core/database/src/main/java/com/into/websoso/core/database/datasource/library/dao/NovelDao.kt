package com.into.websoso.core.database.datasource.library.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.into.websoso.core.database.WebsosoDatabase
import com.into.websoso.core.database.datasource.library.entity.InDatabaseNovelEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Dao
internal interface NovelDao {
    @Query("SELECT * FROM novels ORDER BY sortIndex ASC")
    fun selectAllNovels(): PagingSource<Int, InDatabaseNovelEntity>

    @Query("SELECT COUNT(*) FROM novels")
    suspend fun selectNovelsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovels(novels: List<InDatabaseNovelEntity>)

    @Query("DELETE FROM novels")
    suspend fun deleteAllNovels()
}

@Module
@InstallIn(SingletonComponent::class)
internal object NovelDaoModule {
    @Provides
    @Singleton
    internal fun provideNovelDao(database: WebsosoDatabase): NovelDao = database.novelDao()
}
