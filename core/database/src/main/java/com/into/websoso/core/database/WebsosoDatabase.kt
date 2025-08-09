package com.into.websoso.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.into.websoso.core.database.datasource.library.dao.NovelDao
import com.into.websoso.core.database.datasource.library.entity.InDatabaseNovelEntity

@Database(
    entities = [InDatabaseNovelEntity::class],
    version = 5,
    exportSchema = false,
)
@TypeConverters(Converters::class)
internal abstract class WebsosoDatabase : RoomDatabase() {
    internal abstract fun novelDao(): NovelDao
}
