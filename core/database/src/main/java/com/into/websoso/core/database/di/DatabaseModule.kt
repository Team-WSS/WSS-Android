package com.into.websoso.core.database.di

import android.content.Context
import androidx.room.Room
import com.into.websoso.core.database.WebsosoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
