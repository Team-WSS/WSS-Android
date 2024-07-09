package com.teamwss.websoso.ui.feed.adapter

import com.teamwss.websoso.ui.feed.model.FeedModel

sealed class FeedType {

    data class Feed(
        val feed: FeedModel,
    ) : FeedType()

    data object Loading : FeedType()

    enum class ItemType {
        FEED, LOADING;

        companion object {

            fun valueOf(ordinal: Int): ItemType =
                values().find { it.ordinal == ordinal } ?: throw IllegalArgumentException()
        }
    }
}
