package com.teamwss.websoso.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogRemovePopupMenuBinding
import com.teamwss.websoso.databinding.DialogReportPopupMenuBinding
import com.teamwss.websoso.databinding.FragmentFeedBinding
import com.teamwss.websoso.databinding.MenuFeedPopupBinding
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.ui.feed.adapter.FeedAdapter
import com.teamwss.websoso.ui.feed.adapter.FeedType.Feed
import com.teamwss.websoso.ui.feed.adapter.FeedType.Loading
import com.teamwss.websoso.ui.feed.dialog.FeedRemoveDialogFragment
import com.teamwss.websoso.ui.feed.dialog.FeedReportDialogFragment
import com.teamwss.websoso.ui.feed.model.CategoryModel
import com.teamwss.websoso.ui.feed.model.FeedUiState
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.common.util.toFloatScaledByPx
import com.teamwss.websoso.common.util.toIntScaledByPx
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>(R.layout.fragment_feed) {
    private var _popupBinding: MenuFeedPopupBinding? = null
    private val popupBinding: MenuFeedPopupBinding
        get() = _popupBinding ?: error("error: binding is null")
    private val feedViewModel: FeedViewModel by viewModels()
    private val feedAdapter: FeedAdapter by lazy { FeedAdapter(onClickFeedItem()) }
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _popupBinding = MenuFeedPopupBinding.inflate(inflater, container, false)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun onClickFeedItem() = object : FeedItemClickListener {
        override fun onProfileClick(id: Long) {
            // ProfileActivity.from(context, id)
        }

        override fun onMoreButtonClick(view: View, feedId: Long, isMyFeed: Boolean) {
            showMenu(view, feedId, isMyFeed)
        }

        override fun onContentClick(id: Long) {
            navigateToFeedDetail(id)
        }

        override fun onNovelInfoClick(id: Long) {
            // navigateToNovelDetail(id)
        }

        override fun onLikeButtonClick(view: View, id: Long) {
            val likeCount: Int =
                view.findViewById<TextView>(R.id.tv_feed_thumb_up_count).text.toString().toInt()
            val updatedLikeCount: Int = when (view.isSelected) {
                true -> if (likeCount > 0) likeCount - 1 else 0
                false -> likeCount + 1
            }

            view.findViewById<TextView>(R.id.tv_feed_thumb_up_count).text =
                updatedLikeCount.toString()
            view.isSelected = !view.isSelected

            singleEventHandler.debounce(coroutineScope = lifecycleScope) {
                feedViewModel.updateLike(id, view.isSelected, updatedLikeCount)
            }
        }

        override fun onCommentButtonClick(feedId: Long) {
            navigateToFeedDetail(feedId)
        }
    }

    private fun showMenu(view: View, feedId: Long, isMyFeed: Boolean) {
        val popupWindow: PopupWindow = PopupWindow(
            popupBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 2f
            showAsDropDown(view)
        }

        bindMenuByIsMyFeed(popupWindow, isMyFeed, feedId)
    }

    private fun bindMenuByIsMyFeed(popup: PopupWindow, isMyFeed: Boolean, feedId: Long) {
        with(popupBinding) {
            when (isMyFeed) {
                true -> setupMyFeed(feedId, popup)
                false -> setupNotMyFeed(feedId, popup)
            }
        }
    }

    private fun MenuFeedPopupBinding.setupNotMyFeed(
        feedId: Long,
        popup: PopupWindow,
    ) {
        onFirstItemClick = {
            showDialog<DialogReportPopupMenuBinding>(
                title = getString(R.string.report_popup_menu_spoiling_feed),
                event = { feedViewModel.updateReportedSpoilerFeed(feedId) },
            )
            popup.dismiss()
        }
        onSecondItemClick = {
            showDialog<DialogReportPopupMenuBinding>(
                title = getString(R.string.report_popup_menu_impertinence_feed),
                event = { feedViewModel.updateReportedImpertinenceFeed(feedId) },
            )
            popup.dismiss()
        }
        menuContentTitle =
            getString(R.string.feed_popup_menu_content_report_isNotMyFeed).split(",")
        tvFeedPopupFirstItem.isSelected = false
        tvFeedPopupSecondItem.isSelected = false
    }

    private fun MenuFeedPopupBinding.setupMyFeed(
        feedId: Long,
        popup: PopupWindow,
    ) {
        onFirstItemClick = {
            navigateToFeedEdit(feedId)
            popup.dismiss()
        }
        onSecondItemClick = {
            showDialog<DialogRemovePopupMenuBinding>(
                event = { feedViewModel.updateRemovedFeed(feedId) },
            )
            popup.dismiss()
        }
        menuContentTitle =
            getString(R.string.feed_popup_menu_content_isMyFeed).split(",")
        tvFeedPopupFirstItem.isSelected = true
        tvFeedPopupSecondItem.isSelected = true
    }

    private inline fun <reified Dialog : ViewDataBinding> showDialog(
        title: String? = null,
        noinline event: () -> Unit,
    ) {
        when (Dialog::class) {
            DialogRemovePopupMenuBinding::class -> FeedRemoveDialogFragment.newInstance(
                event = { event() },
            ).show(childFragmentManager, FeedRemoveDialogFragment.TAG)

            DialogReportPopupMenuBinding::class -> FeedReportDialogFragment.newInstance(
                title = title ?: throw IllegalArgumentException(),
                event = { event() },
            ).show(childFragmentManager, FeedReportDialogFragment.TAG)
        }
    }

    fun interface FeedDialogClickListener : Serializable {
        operator fun invoke()
    }

    private fun navigateToFeedEdit(feedId: Long) {
        // 피드 수정 뷰
    }

    private fun navigateToFeedDetail(id: Long) {
        startActivity(FeedDetailActivity.getIntent(id, requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        feedViewModel.updateFeeds()
        observeUiState()
    }

    private fun initView() {
        binding.onWriteClick = ::navigateToFeedWriting
        feedViewModel.categories.setUpChips()
        setupAdapter()
        setupRefreshView()
    }

    private fun navigateToFeedWriting() {
        // 피드 작성 뷰
    }

    private fun List<CategoryModel>.setUpChips() {
        forEach { categoryUiState ->
            WebsosoChip(requireContext()).apply {
                setWebsosoChipText(categoryUiState.category.titleKr)
                setWebsosoChipTextAppearance(R.style.title3)
                setWebsosoChipTextColor(R.color.bg_feed_chip_text_selector)
                setWebsosoChipBackgroundColor(R.color.bg_feed_chip_background_selector)
                setWebsosoChipPaddingVertical(12f.toFloatScaledByPx())
                setWebsosoChipPaddingHorizontal(8f.toFloatScaledByPx())
                setWebsosoChipRadius(18f.toFloatScaledByPx())
                setWebsosoChipSelected(categoryUiState.isSelected)
                setOnWebsosoChipClick { feedViewModel.updateSelectedCategory(categoryUiState.category) }
            }.also { websosoChip -> binding.wcgFeed.addChip(websosoChip) }
        }
    }

    private fun setupAdapter() {
        binding.rvFeed.apply {
            adapter = feedAdapter
            itemAnimator = null
            addOnScrollListener(
                FeedScrollListener.of(
                    singleEventHandler = singleEventHandler,
                    event = feedViewModel::updateFeeds,
                )
            )
            setHasFixedSize(true)
        }
    }

    private fun setupRefreshView() {
        binding.sptrFeedRefresh.apply {
            setRefreshViewParams(
                params = ViewGroup.LayoutParams(
                    30.toIntScaledByPx(), 30.toIntScaledByPx(),
                )
            )
            setLottieAnimation("lottie_websoso_loading.json")
            setOnRefreshListener {
                feedViewModel.updateRefreshedFeeds()
            }
        }
    }

    private fun observeUiState() {
        feedViewModel.feedUiState.observe(viewLifecycleOwner) { feedUiState ->
            when {
                feedUiState.loading -> binding.wllFeed.setWebsosoLoadingVisibility(true)
                feedUiState.error -> binding.wllFeed.setLoadingLayoutVisibility(false)
                !feedUiState.loading -> {
                    binding.wllFeed.setWebsosoLoadingVisibility(false)
                    binding.sptrFeedRefresh.setRefreshing(false)
                    updateFeeds(feedUiState)
                }
            }
        }
    }

    private fun updateFeeds(feedUiState: FeedUiState) {
        binding.clFeedNone.isVisible = feedUiState.feeds.isEmpty()
        val feeds = feedUiState.feeds.map { Feed(it) }
        when (feedUiState.isLoadable) {
            true -> feedAdapter.submitList(feeds + Loading)
            false -> feedAdapter.submitList(feeds)
        }
    }

    override fun onDestroyView() {
        _popupBinding = null
        super.onDestroyView()
    }
}
