package com.teamwss.websoso.ui.feedDetail

import android.content.Context
import android.content.Intent
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityFeedDetailBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedDetailActivity :
    BindingActivity<ActivityFeedDetailBinding>(R.layout.activity_feed_detail) {

    companion object {
        private const val FEED_ID = "FEED_ID"

        fun from(
            id: Long,
            context: Context,
        ): Intent = Intent(context, FeedDetailActivity::class.java).apply {
            putExtra(FEED_ID, id)
        }
    }
}
