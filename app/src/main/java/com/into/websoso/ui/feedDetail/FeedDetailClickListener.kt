package com.into.websoso.ui.feedDetail

import android.view.View

interface FeedDetailClickListener {

    fun onLikeButtonClick(view: View, feedId: Long)

    fun onNovelInfoClick(novelId: Long)

    fun onProfileClick(userId: Long, isMyFeed: Boolean)

    fun onFeedDetailClick(view: View)
}
