package com.into.websoso.ui.feedDetail.model

import androidx.annotation.DrawableRes
import com.into.websoso.data.model.FeedDetailEntity
import com.into.websoso.ui.main.feed.model.FeedModel

data class FeedDetailModel(
    val feed: FeedModel? = null,
    val comments: List<CommentModel> = emptyList(),
    val user: UserModel? = null,
    val novel: FeedDetailEntity.NovelEntity? = null,
) {
    @get:DrawableRes
    val novelImage: Int
        get() = novel?.genre?.let { tag ->
            Genre.from(tag).drawableRes
        } ?: Genre.ROMANCE.drawableRes

    data class UserModel(
        val avatarImage: String,
    )
}
