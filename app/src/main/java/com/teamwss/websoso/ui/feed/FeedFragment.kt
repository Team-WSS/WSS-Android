package com.teamwss.websoso.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.created.team201.presentation.common.BindingFragment
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentFeedBinding
import com.teamwss.websoso.ui.feed.adapter.FeedAdapter

class FeedFragment : BindingFragment<FragmentFeedBinding>(R.layout.fragment_feed) {
    private val feedViewModel: FeedViewModel by viewModels { FeedViewModel.Factory }
    private val feedAdapter: FeedAdapter by lazy { FeedAdapter(onClickFeedItem()) }

    private fun onClickFeedItem() = object : FeedItemClickListener {
        override fun onProfileClick(id: Int) {
            TODO("Not yet implemented")
        }

        override fun onMoreButtonClick() {
            TODO("Not yet implemented")
        }

        override fun onContentClick(id: Int) {
            TODO("Not yet implemented")
        }

        override fun onNovelInfoClick(id: Int) {
            TODO("Not yet implemented")
        }

        override fun onThumbUpButtonClick() {
            TODO("Not yet implemented")
        }

        override fun onCommentButtonClick() {
            TODO("Not yet implemented")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBinding()
        setupAdapter()
        observeFeeds()
    }

    private fun setupBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupAdapter() {
        binding.rvFeed.adapter = feedAdapter
        binding.rvFeed.setHasFixedSize(true)
    }

    private fun observeFeeds() {
        feedViewModel.uiState.observe(viewLifecycleOwner) {
            feedAdapter.submitList(it)
        }
    }
}