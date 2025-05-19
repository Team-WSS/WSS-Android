package com.into.websoso.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.into.websoso.core.database.datasource.library.NovelDao
import com.into.websoso.core.database.entity.NovelEntity

@Database(
    entities = [NovelEntity::class],
    version = 1,
    exportSchema = false,
)
internal abstract class WebsosoDatabase : RoomDatabase() {
    internal abstract fun novelDao(): NovelDao
}
