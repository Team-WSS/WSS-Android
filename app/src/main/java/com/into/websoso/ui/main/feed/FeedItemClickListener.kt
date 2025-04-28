package com.into.websoso.ui.main.feed

import android.view.View

interface FeedItemClickListener {
    fun onProfileClick(
        userId: Long,
        isMyFeed: Boolean,
    )

    fun onMoreButtonClick(
        view: View,
        feedId: Long,
        isMyFeed: Boolean,
    )

    fun onContentClick(feedId: Long)

    fun onNovelInfoClick(novelId: Long)

    fun onLikeButtonClick(
        view: View,
        id: Long,
    )
}
