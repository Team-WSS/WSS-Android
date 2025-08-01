package com.into.websoso.core.database.datasource.library.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.into.websoso.core.model.MyFeed

@Entity(tableName = "novels")
internal data class InDatabaseNovelEntity(
    @PrimaryKey val userNovelId: Long,
    val novelId: Long,
    val title: String,
    val sortIndex: Int,
    val novelImage: String,
    val novelRating: Float,
    val readStatus: String,
    val isInterest: Boolean,
    val userNovelRating: Float,
    val attractivePoints: List<String>,
    val startDate: String,
    val endDate: String,
    val keywords: List<String>,
    val myFeeds: List<String>,
    val feeds: List<MyFeed>,
)
