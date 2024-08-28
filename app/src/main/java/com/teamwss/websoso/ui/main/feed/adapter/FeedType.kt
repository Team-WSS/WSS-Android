package com.teamwss.websoso.ui.main.feed.adapter

import com.teamwss.websoso.ui.main.feed.model.FeedModel

sealed interface FeedType {

    data class Feed(
        val feed: FeedModel,
    ) : FeedType

    data object Loading : FeedType

    enum class ItemType {
        FEED, LOADING;

        companion object {

            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal } ?: throw IllegalArgumentException()
        }
    }
}
