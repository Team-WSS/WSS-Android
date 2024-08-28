package com.teamwss.websoso.ui.main.feed

import android.view.View

interface FeedItemClickListener {

    fun onProfileClick(id: Long)

    fun onMoreButtonClick(view: View, feedId: Long, isMyFeed: Boolean)

    fun onContentClick(id: Long)

    fun onNovelInfoClick(id: Long)

    fun onLikeButtonClick(view: View, id: Long)

    fun onCommentButtonClick(feedId: Long)
}
