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
import com.teamwss.websoso.ui.feed.model.Category.Companion.toWrappedCategories
import com.teamwss.websoso.ui.feed.model.FeedModel
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
        override fun onProfileClick(id: Int) {
            // ProfileActivity.from(context, id)
        }

        override fun onMoreButtonClick(view: View, feedId: Int, userId: Int) {
            showMenu(view, feedId, userId)
        }

        override fun onContentClick(id: Int) {
            navigateToFeedDetail(id)
        }

        override fun onNovelInfoClick(id: Int) {
            // navigateToNovelDetail(id)
        }

        override fun onThumbUpButtonClick(view: View, id: Int) {
            feedViewModel.updateLikeCount(view.isSelected, id)
        }

        override fun onCommentButtonClick() {
            navigateToFeedDetail(id)
        }
    }

    @SuppressLint("InflateParams")
    private fun showMenu(view: View, feedId: Int, userId: Int) {
        PopupWindow(
            popupBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            popupBinding.feedId = feedId
            popupBinding.userId = userId
            showAsDropDown(view)
        }
    }

    private fun navigateToFeedDetail(id: Int) {
        startActivity(
            FeedDetailActivity.from(
                id, requireContext()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupPopupBinding()
        observeUiState()
    }

    private fun setupAdapter() {
        binding.rvFeed.adapter = feedAdapter
        binding.rvFeed.setHasFixedSize(true)
    }

    private fun setupPopupBinding() {
        popupBinding.lifecycleOwner = viewLifecycleOwner
        popupBinding.viewModel = feedViewModel
    }

    private fun observeUiState() {
        feedViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            if (uiState.loading) loading()
            if (uiState.error) throw IllegalStateException()
            if (!uiState.loading) initView(uiState.feeds)
        }
    }

    private fun loading() {

    }

    private fun initView(feeds: List<FeedModel>) {
        setupCategory()
        setupFeeds(feeds)
    }

    private fun setupCategory() {
        when (feedViewModel.gender) {
            "MALE" -> getString(R.string.feed_category_male).toWrappedCategories()
            "FEMALE" -> getString(R.string.feed_category_female).toWrappedCategories()
            else -> throw IllegalStateException()
        }.apply { setUpChips() }
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
                setOnWebsosoChipClick { feedViewModel.fetchFeedsByCategory(category) }
            }.also { websosoChip -> binding.wcgFeed.addChip(websosoChip) }
        }
    }

    private fun setupFeeds(feeds: List<FeedModel>) {
        feedAdapter.submitList(feeds)
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
