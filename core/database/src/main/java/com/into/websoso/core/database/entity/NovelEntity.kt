//package com.into.websoso.core.database.entity
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import com.into.websoso.data.library.model.NovelEntity
//
//@Entity(tableName = "novels")
//internal data class InDatabaseNovelEntity(
//    @PrimaryKey
//    val userNovelId: Long,
//    val novelId: Long,
//    val author: String,
//    val title: String,
//    val novelImage: String,
//    val novelRating: Float,
//) {
//    internal fun toData(): NovelEntity =
//        NovelEntity(
//            userNovelId = userNovelId,
//            novelId = novelId,
//            author = author,
//            title = title,
//            novelImage = novelImage,
//            novelRating = novelRating,
//        )
//}
//
//internal fun NovelEntity.toInDatabaseEntity(): InDatabaseNovelEntity =
//    InDatabaseNovelEntity(
//        userNovelId = userNovelId,
//        novelId = novelId,
//        author = author,
//        title = title,
//        novelImage = novelImage,
//        novelRating = novelRating,
//    )
