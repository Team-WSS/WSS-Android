package com.teamwss.websoso.ui.feed

interface FeedItemClickListener {

    fun onProfileClick(id: Int)

    fun onMoreButtonClick()

    fun onContentClick(id: Int)

    fun onNovelInfoClick(id: Int)

    fun onThumbUpButtonClick()

    fun onCommentButtonClick()
}