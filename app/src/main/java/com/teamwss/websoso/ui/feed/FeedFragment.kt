package com.teamwss.websoso.ui.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentFeedBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.feed.adapter.FeedAdapter
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity

class FeedFragment : BindingFragment<FragmentFeedBinding>(R.layout.fragment_feed) {
    private val feedViewModel: FeedViewModel by viewModels { FeedViewModel.Factory }
    private val feedAdapter: FeedAdapter by lazy { FeedAdapter(onClickFeedItem()) }

    private fun onClickFeedItem() = object : FeedItemClickListener {
        override fun onProfileClick(id: Int) {
            // ProfileActivity.from(context, id)
        }

        override fun onMoreButtonClick(view: View) {
            showMenu(view)
        }

        override fun onContentClick(id: Int) {
            navigateToFeedDetail(id)
        }

        override fun onNovelInfoClick(id: Int) {
            // navigateToNovelDetail(id)
        }

        override fun onThumbUpButtonClick() {
            // feedViewModel.updateThumbUp()
        }

        override fun onCommentButtonClick() {
            navigateToFeedDetail(id)
        }
    }

    private fun navigateToFeedDetail(id: Int) {
        startActivity(
            FeedDetailActivity.from(
                id, context ?: throw IllegalArgumentException("추후 에러 헨들링 요망")
            )
        )
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

    @SuppressLint("InflateParams")
    fun showMenu(view: View) {
        val popupView = layoutInflater.inflate(R.layout.menu_feed_popup, null)
        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.showAsDropDown(view)
    }
}
