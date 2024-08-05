package com.teamwss.websoso.ui.feedDetail.adapter

import com.teamwss.websoso.ui.feedDetail.model.FeedDetailModel
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailModel.CommentModel

sealed class FeedDetailItemType {

    data class Header(
        val feed: FeedDetailModel,
    ) : FeedDetailItemType()

    data class Comment(
        val comment: CommentModel,
    ) : FeedDetailItemType()

    enum class ItemType {
        HEADER, COMMENT;

        companion object {

            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal } ?: throw IllegalArgumentException()
        }
    }
}
