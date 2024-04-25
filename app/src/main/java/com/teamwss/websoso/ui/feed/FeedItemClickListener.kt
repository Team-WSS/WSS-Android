package com.teamwss.websoso.ui.feed

import android.view.View

interface FeedItemClickListener {

    fun onProfileClick(id: Long)

    fun onMoreButtonClick(view: View, feedId: Long, userId: Long)

    fun onContentClick(id: Long)

    fun onNovelInfoClick(id: Long)

    fun onLikeButtonClick(view: View, id: Long)

    fun onCommentButtonClick(feedId: Long)
}
