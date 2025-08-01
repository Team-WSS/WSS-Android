package com.into.websoso.data.library.model

import com.into.websoso.core.model.MyFeed

data class UserNovelsEntity(
    val isLoadable: Boolean,
    val userNovelCount: Long,
    val userNovels: List<NovelEntity>,
)

data class NovelEntity(
    val userNovelId: Long,
    val novelId: Long,
    val title: String,
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
    val feeds: List<MyFeed> = dummy,
)

private val dummy = listOf(
    MyFeed(
        id = 0L,
        content = "피드입니둥",
    ),
)
