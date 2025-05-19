package com.into.websoso.core.database.datasource.library

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.into.websoso.core.database.WebsosoDatabase
import com.into.websoso.core.database.entity.NovelEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
internal interface NovelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNovels(novels: List<NovelEntity>)

    @Query(
        value = """
        SELECT * FROM novels
        ORDER BY userNovelId
        DESC
    """,
    )
    fun selectAllNovels(): Flow<List<NovelEntity>>

    @Query(
        value = """
        SELECT * FROM novels
        WHERE userNovelId = :id
    """,
    )
    suspend fun selectNovelById(id: Long): NovelEntity?

    @Query(
        value = """
        DELETE FROM novels
    """,
    )
    suspend fun deleteAllNovels()
}

@Module
@InstallIn(SingletonComponent::class)
internal object NovelDaoModule {
    @Provides
    @Singleton
    internal fun provideNovelDao(database: WebsosoDatabase): NovelDao = database.novelDao()
}
