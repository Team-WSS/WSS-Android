package com.into.websoso.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.into.websoso.core.database.datasource.library.NovelDao
import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [InDatabaseNovelEntity::class],
    version = 1,
    exportSchema = false,
)
internal abstract class WebsosoDatabase : RoomDatabase() {
    internal abstract fun novelDao(): NovelDao
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
