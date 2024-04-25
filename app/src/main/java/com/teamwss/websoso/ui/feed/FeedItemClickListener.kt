package com.teamwss.websoso.ui.feed

import android.view.View

interface FeedItemClickListener {

    fun onProfileClick(id: Int)

    fun onMoreButtonClick(view: View, feedId: Int, userId: Int)

    fun onContentClick(id: Int)

    fun onNovelInfoClick(id: Int)

    fun onThumbUpButtonClick(view: View, id: Int)

    fun onCommentButtonClick()
}
