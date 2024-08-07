package com.teamwss.websoso.ui.feedDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityFeedDetailBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailAdapter
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.Comment
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.Header
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Error
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Loading
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Success
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedDetailActivity :
    BindingActivity<ActivityFeedDetailBinding>(R.layout.activity_feed_detail) {
    private val feedDetailViewModel: FeedDetailViewModel by viewModels()
    private val feedId: Long by lazy { intent.getLongExtra(FEED_ID, DEFAULT_FEED_ID) }
    private val feedDetailAdapter: FeedDetailAdapter by lazy { FeedDetailAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        setupObserver()
    }

    private fun initView() {
        feedDetailViewModel.updateFeedDetail(feedId)
        binding.rvFeedDetail.adapter = feedDetailAdapter
    }

    private fun setupObserver() {
        feedDetailViewModel.feedDetailUiState.observe(this) { feedDetailUiState ->
            when (feedDetailUiState) {
                is Success -> updateView(feedDetailUiState)
                is Loading -> {}
                is Error -> {}
            }
        }
    }

    private fun updateView(feedDetailUiState: Success) {
        val header = Header(feedDetailUiState.feedDetail.feed)
        val comments = feedDetailUiState.feedDetail.comments.map { Comment(it) }

        feedDetailAdapter.submitList(listOf(header) + comments)
    }

    companion object {
        private const val FEED_ID: String = "FEED_ID"
        private const val DEFAULT_FEED_ID: Long = -1

        fun getIntent(id: Long, context: Context): Intent =
            Intent(context, FeedDetailActivity::class.java).apply { putExtra(FEED_ID, id) }
    }
}
