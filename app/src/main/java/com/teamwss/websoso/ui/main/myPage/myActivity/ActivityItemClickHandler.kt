package com.teamwss.websoso.ui.main.myPage.myActivity

import android.content.Context
import android.view.View
import android.widget.TextView
import com.teamwss.websoso.R
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity

class ActivityItemClickHandler(
    private val context: Context,
    private val myActivityViewModel: MyActivityViewModel
) : ActivityItemClickListener {

    override fun onContentClick(feedId: Long) {
        context.startActivity(FeedDetailActivity.getIntent(context, feedId))
    }

    override fun onNovelInfoClick(novelId: Long) {
        context.startActivity(NovelDetailActivity.getIntent(context, novelId))
    }

    override fun onLikeButtonClick(view: View, feedId: Long) {
        val likeCountTextView: TextView = view.findViewById(R.id.tv_my_activity_thumb_up_count)

        val currentLikeCount = likeCountTextView.text.toString().toInt()

        val updatedLikeCount: Int = if (view.isSelected) {
            if (currentLikeCount > 0) currentLikeCount - 1 else 0
        } else {
            currentLikeCount + 1
        }

        likeCountTextView.text = updatedLikeCount.toString()
        view.isSelected = !view.isSelected

        myActivityViewModel.saveLike(view.isSelected, feedId, updatedLikeCount)
    }

    override fun onMoreButtonClick(view: View, feedId: Long) {
        //TODO 팝업메뉴 수정 and 차단
    }
}
