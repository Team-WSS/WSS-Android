package com.teamwss.websoso.ui.feed

import android.view.View

interface FeedItemClickListener {

    fun onProfileClick(id: Int)

    fun onMoreButtonClick(view: View)

    fun onContentClick(id: Int)

    fun onNovelInfoClick(id: Int)

    fun onThumbUpButtonClick()

    fun onCommentButtonClick()
}
