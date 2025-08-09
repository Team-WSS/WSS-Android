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

    @Query("SELECT * FROM novels ORDER BY sortIndex DESC LIMIT 1")
    suspend fun selectLastNovel(): InDatabaseNovelEntity?

    @Query("SELECT COUNT(*) FROM novels")
    suspend fun selectNovelsCount(): Int

    @Query("SELECT * FROM novels WHERE userNovelId = :userNovelId LIMIT 1")
    suspend fun selectNovelByUserNovelId(userNovelId: Long): InDatabaseNovelEntity?

    @Query("SELECT * FROM novels WHERE novelId = :novelId LIMIT 1")
    suspend fun selectNovelByNovelId(novelId: Long): InDatabaseNovelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovels(novels: List<InDatabaseNovelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovel(novel: InDatabaseNovelEntity)

    @Query("DELETE FROM novels")
    suspend fun deleteAllNovels()

    @Query("DELETE FROM novels WHERE novelId = :novelId")
    suspend fun deleteNovel(novelId: Long)
}

@Module
@InstallIn(SingletonComponent::class)
internal object NovelDaoModule {
    @Provides
    @Singleton
    internal fun provideNovelDao(database: WebsosoDatabase): NovelDao = database.novelDao()
}
