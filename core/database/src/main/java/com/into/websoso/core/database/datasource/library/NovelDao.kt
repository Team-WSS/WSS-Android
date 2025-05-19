package com.into.websoso.core.database.datasource.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.into.websoso.core.database.WebsosoDatabase
import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
internal interface NovelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovels(novels: List<InDatabaseNovelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovel(novel: InDatabaseNovelEntity)

    @Query("SELECT * FROM novels ORDER BY userNovelId DESC")
    fun selectAllNovels(): Flow<List<InDatabaseNovelEntity>>

    @Query("SELECT * FROM novels WHERE userNovelId = :id")
    suspend fun selectNovelById(id: Long): InDatabaseNovelEntity

    @Update
    suspend fun updateNovels(novels: List<InDatabaseNovelEntity>)

    @Update
    suspend fun updateNovel(novel: InDatabaseNovelEntity)

    @Query("DELETE FROM novels")
    suspend fun deleteAllNovels()

    @Query("DELETE FROM novels WHERE userNovelId = :id")
    suspend fun deleteNovelById(id: Long)
}

@Module
@InstallIn(SingletonComponent::class)
internal object NovelDaoModule {
    @Provides
    @Singleton
    internal fun provideNovelDao(database: WebsosoDatabase): NovelDao = database.novelDao()
}
