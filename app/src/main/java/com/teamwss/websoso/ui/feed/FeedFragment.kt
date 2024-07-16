package com.teamwss.websoso.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogRemovePopupMenuBinding
import com.teamwss.websoso.databinding.DialogReportPopupMenuBinding
import com.teamwss.websoso.databinding.FragmentFeedBinding
import com.teamwss.websoso.databinding.MenuFeedPopupBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.feed.adapter.FeedAdapter
import com.teamwss.websoso.ui.feed.adapter.FeedType.Feed
import com.teamwss.websoso.ui.feed.adapter.FeedType.Loading
import com.teamwss.websoso.ui.feed.dialog.FeedRemoveDialogFragment
import com.teamwss.websoso.ui.feed.dialog.FeedReportDialogFragment
import com.teamwss.websoso.ui.feed.model.CategoryModel
import com.teamwss.websoso.ui.feed.model.FeedUiState
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.util.SingleEventHandler
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class FeedFragment : BindingFragment<FragmentFeedBinding>(R.layout.fragment_feed) {
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
            elevation = 12f
            showAsDropDown(view)
        }

        bindMenuByIsMyFeed(popupWindow, isMyFeed, feedId)
    }

    private fun bindMenuByIsMyFeed(popup: PopupWindow, isMyFeed: Boolean, feedId: Long) {
        with(popupBinding) {
            when (isMyFeed) {
                true -> {
                    tvFeedPopupFirstItem.isSelected = true
                    tvFeedPopupSecondItem.isSelected = true
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
                }

                false -> {
                    tvFeedPopupFirstItem.isSelected = false
                    tvFeedPopupSecondItem.isSelected = false
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
                }
            }
        }
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
        startActivity(FeedDetailActivity.from(id, requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeUiState()
    }

    private fun initView() {
        feedViewModel.categories.setUpChips()
        feedViewModel.updateFeeds()
        setupAdapter()
    }

    private fun List<CategoryModel>.setUpChips() {
        forEach { categoryUiState ->
            WebsosoChip(requireContext()).apply {
                setWebsosoChipText(categoryUiState.category.titleKr)
                setWebsosoChipTextAppearance(R.style.title3)
                setWebsosoChipTextColor(R.color.bg_feed_chip_text_selector)
                setWebsosoChipStrokeColor(R.color.bg_feed_chip_stroke_selector)
                setWebsosoChipBackgroundColor(R.color.bg_feed_chip_background_selector)
                setWebsosoChipPaddingVertical(20f)
                setWebsosoChipPaddingHorizontal(12f)
                setWebsosoChipRadius(30f)
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
                    event = feedViewModel::updateFeeds
                )
            )
            setHasFixedSize(true)
        }
    }

    private fun observeUiState() {
        feedViewModel.feedUiState.observe(viewLifecycleOwner) { feedUiState ->
            when {
                feedUiState.loading -> loading()
                feedUiState.error -> throw IllegalStateException()
                !feedUiState.loading -> updateFeeds(feedUiState)
            }
        }
    }

    private fun loading() {
        // 로딩 뷰
    }

    private fun updateFeeds(feedUiState: FeedUiState) {
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
