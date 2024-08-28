package com.teamwss.websoso.ui.feedDetail.adapter

import com.teamwss.websoso.ui.main.feed.model.FeedModel
import com.teamwss.websoso.ui.feedDetail.model.CommentModel

sealed interface FeedDetailType {

    data class Header(
        val feed: FeedModel,
    ) : FeedDetailType

    data class Comment(
        val comment: CommentModel,
    ) : FeedDetailType

    enum class ItemType {
        HEADER, COMMENT;

        companion object {

            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal } ?: throw IllegalArgumentException()
        }
    }
}
