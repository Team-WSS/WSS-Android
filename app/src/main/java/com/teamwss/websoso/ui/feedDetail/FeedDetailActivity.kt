package com.teamwss.websoso.ui.feedDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityFeedDetailBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedDetailActivity :
    BindingActivity<ActivityFeedDetailBinding>(R.layout.activity_feed_detail) {
    private val feedDetailViewModel: FeedDetailViewModel by viewModels()
    private val feedId: Long by lazy { intent.getLongExtra(FEED_ID, DEFAULT_FEED_ID) }
    private val feedDetailAdapter: FeedDetailAdapter by lazy { FeedDetailAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rvFeedDetail.adapter = feedDetailAdapter
    }

    companion object {
        private const val FEED_ID: String = "FEED_ID"
        private const val DEFAULT_FEED_ID: Long = -1

        fun from(
            id: Long,
            context: Context,
        ): Intent = Intent(context, FeedDetailActivity::class.java).apply {
            putExtra(FEED_ID, id)
        }
    }
}
