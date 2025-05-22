package com.into.websoso.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "novels")
internal data class NovelEntity(
    @PrimaryKey
    val userNovelId: Long,
    val novelId: Long,
    val author: String,
    val title: String,
    val novelImage: String,
    val novelRating: Float,
)
