package com.teamwss.websoso.ui.feed.adapter

import com.teamwss.websoso.ui.feed.model.FeedModel

sealed class FeedItemType {
    val viewType: Int
        get() = when (this) {
            is Feed -> FEED
            is Loading -> LOADING
        }

    data class Feed(
        val feed: FeedModel,
    ) : FeedItemType()

    data object Loading : FeedItemType()

    companion object {
        const val FEED = 1
        const val LOADING = 2

        fun valueOf(i:Int){

        }
    }
}
