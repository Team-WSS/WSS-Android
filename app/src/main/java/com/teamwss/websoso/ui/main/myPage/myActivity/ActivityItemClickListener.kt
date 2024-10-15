package com.teamwss.websoso.ui.main.myPage.myActivity

import android.view.View

interface ActivityItemClickListener {
    fun onContentClick(feedId: Long)

    fun onNovelInfoClick(novelId: Long)

    fun onLikeButtonClick(view: View, feedId: Long)

    fun onMoreButtonClick(view: View, feedId: Long)
}