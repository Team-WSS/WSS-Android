package com.teamwss.websoso.ui.main.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.common.util.InfiniteScrollListener
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.common.util.toFloatPxFromDp
import com.teamwss.websoso.common.util.toIntPxFromDp
import com.teamwss.websoso.databinding.DialogRemovePopupMenuBinding
import com.teamwss.websoso.databinding.DialogReportPopupMenuBinding
import com.teamwss.websoso.databinding.FragmentFeedBinding
import com.teamwss.websoso.databinding.MenuFeedPopupBinding
import com.teamwss.websoso.ui.createFeed.CreateFeedActivity
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.ui.feedDetail.model.EditFeedModel
import com.teamwss.websoso.ui.main.feed.adapter.FeedAdapter
import com.teamwss.websoso.ui.main.feed.adapter.FeedType.Feed
import com.teamwss.websoso.ui.main.feed.adapter.FeedType.Loading
import com.teamwss.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_FEED
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType
import com.teamwss.websoso.ui.main.feed.model.CategoryModel
import com.teamwss.websoso.ui.main.feed.model.FeedUiState
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
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
    private lateinit var activityResultCallback: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _popupBinding = MenuFeedPopupBinding.inflate(inflater, container, false)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun onClickFeedItem() = object : FeedItemClickListener {
        override fun onProfileClick(userId: Long) {
            singleEventHandler.throttleFirst(300) { navigateToOtherUserPage(userId) }
        }

        override fun onMoreButtonClick(view: View, feedId: Long, isMyFeed: Boolean) {
            singleEventHandler.throttleFirst { showMenu(view, feedId, isMyFeed) }
        }

        override fun onContentClick(feedId: Long) {
            singleEventHandler.throttleFirst(300) { navigateToFeedDetail(feedId) }
        }

        override fun onNovelInfoClick(novelId: Long) {
            singleEventHandler.throttleFirst(300) { navigateToNovelDetail(novelId) }
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
    }

    private fun navigateToOtherUserPage(userId: Long) {
//        singleEventHandler.throttleFirst(300) {
//            OtherUserPageActivity.getIntent(requireContext(), userId)
//        }
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

    private fun MenuFeedPopupBinding.setupMyFeed(feedId: Long, popup: PopupWindow) {
        onFirstItemClick = {
            singleEventHandler.throttleFirst {
                navigateToFeedEdit(feedId)
                popup.dismiss()
            }
        }
        onSecondItemClick = {
            singleEventHandler.throttleFirst {
                showDialog<DialogRemovePopupMenuBinding>(
                    menuType = REMOVE_FEED.name,
                    event = { feedViewModel.updateRemovedFeed(feedId) },
                )
                popup.dismiss()
            }
        }
        menuContentTitle = getString(R.string.feed_popup_menu_content_isMyFeed).split(",")
        tvFeedPopupFirstItem.isSelected = true
        tvFeedPopupSecondItem.isSelected = true
    }

    private fun MenuFeedPopupBinding.setupNotMyFeed(feedId: Long, popup: PopupWindow) {
        onFirstItemClick = {
            singleEventHandler.throttleFirst {
                showDialog<DialogReportPopupMenuBinding>(
                    menuType = ReportMenuType.SPOILER_FEED.name,
                    event = { feedViewModel.updateReportedSpoilerFeed(feedId) },
                )
                popup.dismiss()
            }
        }
        onSecondItemClick = {
            singleEventHandler.throttleFirst {
                showDialog<DialogReportPopupMenuBinding>(
                    menuType = ReportMenuType.IMPERTINENCE_FEED.name,
                    event = { feedViewModel.updateReportedImpertinenceFeed(feedId) },
                )
                popup.dismiss()
            }
        }
        menuContentTitle = getString(R.string.feed_popup_menu_content_report_isNotMyFeed).split(",")
        tvFeedPopupFirstItem.isSelected = false
        tvFeedPopupSecondItem.isSelected = false
    }

    private inline fun <reified Dialog : ViewDataBinding> showDialog(
        menuType: String,
        noinline event: () -> Unit,
    ) {
        when (Dialog::class) {
            DialogRemovePopupMenuBinding::class -> FeedRemoveDialogFragment.newInstance(
                menuType = menuType, event = { event() },
            ).show(childFragmentManager, FeedRemoveDialogFragment.TAG)

            DialogReportPopupMenuBinding::class -> FeedReportDialogFragment.newInstance(
                menuType = menuType, event = { event() },
            ).show(childFragmentManager, FeedReportDialogFragment.TAG)
        }
    }

    fun interface FeedDialogClickListener : Serializable {
        operator fun invoke()
    }

    private fun navigateToFeedEdit(feedId: Long) {
        val feedContent =
            feedViewModel.feedUiState.value?.feeds?.find { it.id == feedId }?.let { feed ->
                EditFeedModel(
                    feedId = feed.id,
                    novelId = feed.novel.id,
                    novelTitle = feed.novel.title,
                    feedContent = feed.content,
                    feedCategory = feed.relevantCategories,
                )
            } ?: throw IllegalArgumentException()

        activityResultCallback.launch(CreateFeedActivity.getIntent(requireContext(), feedContent))
    }

    private fun navigateToFeedDetail(feedId: Long) {
        startActivity(FeedDetailActivity.getIntent(requireContext(), feedId))
    }

    private fun navigateToNovelDetail(novelId: Long) {
        startActivity(NovelDetailActivity.getIntent(requireContext(), novelId))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityResultCallback = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) feedViewModel.updateRefreshedFeeds()
        }
        initView()
        feedViewModel.updateFeeds()
        setupObserver()
    }

    private fun initView() {
        binding.onWriteClick = { singleEventHandler.throttleFirst { navigateToFeedWriting() } }
        feedViewModel.categories.setUpChips()
        setupAdapter()
        setupRefreshView()
    }

    private fun navigateToFeedWriting() {
        activityResultCallback.launch(CreateFeedActivity.getIntent(requireContext()))
    }

    private fun List<CategoryModel>.setUpChips() {
        forEach { categoryUiState ->
            WebsosoChip(requireContext()).apply {
                setWebsosoChipText(categoryUiState.category.titleKr)
                setWebsosoChipTextAppearance(R.style.title3)
                setWebsosoChipTextColor(R.color.bg_feed_chip_text_selector)
                setWebsosoChipBackgroundColor(R.color.bg_feed_chip_background_selector)
                setWebsosoChipPaddingVertical(12f.toFloatPxFromDp())
                setWebsosoChipPaddingHorizontal(8f.toFloatPxFromDp())
                setWebsosoChipRadius(18f.toFloatPxFromDp())
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
                InfiniteScrollListener.of(
                    singleEventHandler = singleEventHandler,
                    event = feedViewModel::updateFeeds,
                )
            )
            setHasFixedSize(true)
        }
    }

    private fun setupRefreshView() {
        binding.sptrFeedRefresh.apply {
            singleEventHandler.throttleFirst {
                setRefreshViewParams(
                    params = ViewGroup.LayoutParams(
                        30.toIntPxFromDp(), 30.toIntPxFromDp(),
                    )
                )
                setLottieAnimation("lottie_websoso_loading.json")
                setOnRefreshListener {
                    feedViewModel.updateRefreshedFeeds()
                }
            }
        }
    }

    private fun setupObserver() {
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
        }.apply {
            binding.rvFeed.smoothScrollToPosition(0)
        }
    }

    override fun onDestroyView() {
        _popupBinding = null
        super.onDestroyView()
    }
}
