package com.teamwss.websoso.ui.feedDetail

import android.view.View

interface CommentClickListener {

    fun onProfileClick(userId: Long, isMyComment: Boolean)

    fun onMoreButtonClick(view: View, commentId: Long, isMyComment: Boolean)
}
