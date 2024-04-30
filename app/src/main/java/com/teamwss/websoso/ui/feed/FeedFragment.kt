package com.teamwss.websoso.ui.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentFeedBinding
import com.teamwss.websoso.databinding.MenuFeedPopupBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.feed.adapter.FeedAdapter
import com.teamwss.websoso.ui.feed.model.Category
import com.teamwss.websoso.ui.feed.model.FeedModel
import com.teamwss.websoso.ui.feed.model.FeedUiState
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity

class FeedFragment : BindingFragment<FragmentFeedBinding>(R.layout.fragment_feed) {
    private var _popupBinding: MenuFeedPopupBinding? = null
    private val popupBinding get() = _popupBinding ?: error("error: binding is null")
    private val feedViewModel: FeedViewModel by viewModels { FeedViewModel.Factory }
    private val feedAdapter: FeedAdapter by lazy { FeedAdapter(onClickFeedItem()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _popupBinding = MenuFeedPopupBinding.inflate(inflater, container, false)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun onClickFeedItem() = object : FeedItemClickListener {
        override fun onProfileClick(id: Long) {
            // ProfileActivity.from(context, id)
        }

        override fun onMoreButtonClick(view: View, feedId: Long, userId: Long) {
            showMenu(view, feedId, userId)
        }

        override fun onContentClick(id: Long) {
            navigateToFeedDetail(id)
        }

        override fun onNovelInfoClick(id: Long) {
            // navigateToNovelDetail(id)
        }

        override fun onLikeButtonClick(view: View, id: Long) {
            feedViewModel.updateLikeCount(view.isSelected, id)
        }

        override fun onCommentButtonClick(feedId: Long) {
            navigateToFeedDetail(feedId)
        }
    }

    @SuppressLint("InflateParams")
    private fun showMenu(view: View, feedId: Long, userId: Long) {
        PopupWindow(
            popupBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            popupBinding.feedId = feedId
            popupBinding.userId = userId
            elevation = 12f
            showAsDropDown(view)
        }
    }

    private fun navigateToFeedDetail(id: Long) {
        startActivity(
            FeedDetailActivity.from(id, requireContext())
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        feedViewModel.category.setUpChips()
        bindViewModel()
        observeUiState()
    }

    private fun List<Category>.setUpChips() {
        forEach { category ->
            WebsosoChip(requireContext()).apply {
                setWebsosoChipText(category.title)
                setWebsosoChipTextAppearance(R.style.title3)
                setWebsosoChipTextColor(R.color.bg_feed_chip_text_selector)
                setWebsosoChipStrokeColor(R.color.bg_feed_chip_stroke_selector)
                setWebsosoChipBackgroundColor(R.color.bg_feed_chip_background_selector)
                setWebsosoChipPaddingVertical(20f)
                setWebsosoChipPaddingHorizontal(12f)
                setWebsosoChipRadius(30f)
                setOnWebsosoChipClick { feedViewModel.updateFeeds(category) }
            }.also { websosoChip ->
                binding.wcgFeed.addChip(websosoChip)
            }
        }
    }

    private fun setupAdapter() {
        binding.rvFeed.apply {
            adapter = feedAdapter
            addOnScrollListener(
                FeedScrollListener.from(this, feedViewModel::updateFeeds)
            )
            setHasFixedSize(true)
        }
    }

    private fun bindViewModel() {
        popupBinding.lifecycleOwner = viewLifecycleOwner
        popupBinding.viewModel = feedViewModel
    }

    private fun observeUiState() {
        feedViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.loading -> loading()
                uiState.error -> throw IllegalStateException()
                !uiState.loading -> updateView(uiState.feeds, uiState.categories)
            }
        }
    }

    private fun updateView(feeds: List<FeedModel>, categories: List<FeedUiState.CategoryUiState>) {
        // binding.wcgFeed.submitList(categories) 구현 예정
        // 값 2번씩 호출됨
        feedAdapter.submitList(feeds)
    }

    private fun loading() {
        // 로딩 뷰
    }

    override fun onStop() {
        super.onStop()
        feedViewModel.saveLikeCount()
    }

    override fun onDestroyView() {
        _popupBinding = null
        super.onDestroyView()
    }
}
