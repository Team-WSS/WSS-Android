package com.into.websoso.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.into.websoso.core.database.dao.FilteredNovelDao
import com.into.websoso.core.database.dao.NovelDao
import com.into.websoso.core.database.entity.InDatabaseFilteredNovelEntity
import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [InDatabaseNovelEntity::class, InDatabaseFilteredNovelEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
internal abstract class WebsosoDatabase : RoomDatabase() {
    internal abstract fun novelDao(): NovelDao

    internal abstract fun filteredNovelDao(): FilteredNovelDao
}

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    internal fun provideDatabase(
        @ApplicationContext context: Context,
    ): WebsosoDatabase =
        Room
            .databaseBuilder(
                context,
                WebsosoDatabase::class.java,
                "websoso.db",
            ).build()
}
