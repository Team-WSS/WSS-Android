package com.teamwss.websoso.ui.main.feed

import android.view.View

interface FeedItemClickListener {

    fun onProfileClick(userId: Long)

    fun onMoreButtonClick(view: View, feedId: Long, isMyFeed: Boolean)

    fun onContentClick(feedId: Long)

    fun onNovelInfoClick(novelId: Long)

    fun onLikeButtonClick(view: View, id: Long)
}
