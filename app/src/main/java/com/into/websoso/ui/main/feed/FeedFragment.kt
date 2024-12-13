package com.into.websoso.ui.main.feed

import android.annotation.SuppressLint
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
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.into.websoso.R
import com.into.websoso.R.drawable.ic_blocked_user_snack_bar
import com.into.websoso.R.drawable.ic_novel_detail_check
import com.into.websoso.R.id.tv_feed_thumb_up_count
import com.into.websoso.R.layout.fragment_feed
import com.into.websoso.R.string.block_user_success_message
import com.into.websoso.R.string.feed_create_done
import com.into.websoso.R.string.feed_popup_menu_content_isMyFeed
import com.into.websoso.R.string.feed_popup_menu_content_report_isNotMyFeed
import com.into.websoso.R.string.feed_removed_feed_snackbar
import com.into.websoso.R.string.feed_server_error
import com.into.websoso.common.ui.base.BaseFragment
import com.into.websoso.common.ui.custom.WebsosoChip
import com.into.websoso.common.ui.model.ResultFrom.BlockUser
import com.into.websoso.common.ui.model.ResultFrom.CreateFeed
import com.into.websoso.common.ui.model.ResultFrom.FeedDetailBack
import com.into.websoso.common.ui.model.ResultFrom.FeedDetailError
import com.into.websoso.common.ui.model.ResultFrom.FeedDetailRemoved
import com.into.websoso.common.ui.model.ResultFrom.NovelDetailBack
import com.into.websoso.common.ui.model.ResultFrom.OtherUserProfileBack
import com.into.websoso.common.ui.model.ResultFrom.WithdrawUser
import com.into.websoso.common.util.InfiniteScrollListener
import com.into.websoso.common.util.SingleEventHandler
import com.into.websoso.common.util.showWebsosoSnackBar
import com.into.websoso.common.util.toFloatPxFromDp
import com.into.websoso.common.util.toIntPxFromDp
import com.into.websoso.databinding.DialogRemovePopupMenuBinding
import com.into.websoso.databinding.DialogReportPopupMenuBinding
import com.into.websoso.databinding.FragmentFeedBinding
import com.into.websoso.databinding.MenuFeedPopupBinding
import com.into.websoso.ui.createFeed.CreateFeedActivity
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.feedDetail.model.EditFeedModel
import com.into.websoso.ui.main.feed.adapter.FeedAdapter
import com.into.websoso.ui.main.feed.adapter.FeedType.Feed
import com.into.websoso.ui.main.feed.adapter.FeedType.Loading
import com.into.websoso.ui.main.feed.adapter.FeedType.NoMore
import com.into.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.into.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.into.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_FEED
import com.into.websoso.ui.main.feed.dialog.ReportMenuType
import com.into.websoso.ui.main.feed.model.CategoryModel
import com.into.websoso.ui.main.feed.model.FeedUiState
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.into.websoso.ui.otherUserPage.BlockUserDialogFragment.Companion.USER_NICKNAME
import com.into.websoso.ui.otherUserPage.OtherUserPageActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Serializable

@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>(fragment_feed) {
    private var _popupBinding: MenuFeedPopupBinding? = null
    private val popupBinding: MenuFeedPopupBinding get() = _popupBinding ?: error("FeedFragment")
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
        override fun onProfileClick(userId: Long, isMyFeed: Boolean) {
            singleEventHandler.throttleFirst(300) { navigateToProfile(userId, isMyFeed) }
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

        @SuppressLint("CutPasteId")
        override fun onLikeButtonClick(view: View, id: Long) {
            val likeCount: Int =
                view.findViewById<TextView>(tv_feed_thumb_up_count).text.toString().toInt()
            val updatedLikeCount: Int = when (view.isSelected) {
                true -> if (likeCount > 0) likeCount - 1 else 0
                false -> likeCount + 1
            }

            view.findViewById<TextView>(tv_feed_thumb_up_count).text = updatedLikeCount.toString()
            view.isSelected = !view.isSelected

            singleEventHandler.debounce(coroutineScope = lifecycleScope) {
                feedViewModel.updateLike(id, view.isSelected, updatedLikeCount)
            }
        }
    }

    private fun navigateToProfile(userId: Long, isMyFeed: Boolean) {
        if (isMyFeed) return

        activityResultCallback.launch(
            OtherUserPageActivity.getIntent(
                requireContext(),
                userId,
            )
        )
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
        menuContentTitle = getString(feed_popup_menu_content_isMyFeed).split(",")
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
        menuContentTitle = getString(feed_popup_menu_content_report_isNotMyFeed).split(",")
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
                    isSpoiler = feed.isSpoiler,
                    feedContent = feed.content,
                    feedCategory = feed.relevantCategories,
                )
            } ?: throw IllegalArgumentException()

        activityResultCallback.launch(CreateFeedActivity.getIntent(requireContext(), feedContent))
    }

    private fun navigateToFeedDetail(feedId: Long) {
        activityResultCallback.launch(FeedDetailActivity.getIntent(requireContext(), feedId))
    }

    private fun navigateToNovelDetail(novelId: Long) {
        activityResultCallback.launch(NovelDetailActivity.getIntent(requireContext(), novelId))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setupObserver()
        refreshView()
    }

    private fun refreshView() {
        if (::activityResultCallback.isInitialized.not()) {
            activityResultCallback = registerForActivityResult(StartActivityForResult()) { result ->
                when (result.resultCode) {
                    FeedDetailBack.RESULT_OK,
                    NovelDetailBack.RESULT_OK,
                    OtherUserProfileBack.RESULT_OK,
                    -> feedViewModel.updateRefreshedFeeds(false)

                    CreateFeed.RESULT_OK -> {
                        feedViewModel.updateRefreshedFeeds(true)

                        showWebsosoSnackBar(
                            view = binding.root,
                            message = getString(feed_create_done),
                            icon = ic_novel_detail_check,
                        )
                    }

                    FeedDetailRemoved.RESULT_OK -> {
                        feedViewModel.updateRefreshedFeeds(false)

                        showWebsosoSnackBar(
                            view = binding.root,
                            message = getString(feed_removed_feed_snackbar),
                            icon = ic_blocked_user_snack_bar,
                        )
                    }

                    FeedDetailError.RESULT_OK -> {
                        feedViewModel.updateRefreshedFeeds(false)

                        showWebsosoSnackBar(
                            view = binding.root,
                            message = getString(feed_server_error),
                            icon = ic_blocked_user_snack_bar,
                        )
                    }

                    BlockUser.RESULT_OK -> {
                        feedViewModel.updateRefreshedFeeds(false)

                        val nickname = result.data?.getStringExtra(USER_NICKNAME).orEmpty()

                        showWebsosoSnackBar(
                            view = binding.root,
                            message = getString(block_user_success_message, nickname),
                            icon = ic_novel_detail_check,
                        )
                    }

                    WithdrawUser.RESULT_OK -> {
                        showWebsosoSnackBar(
                            view = binding.root,
                            message = getString(R.string.other_user_page_withdraw_user),
                            icon = ic_blocked_user_snack_bar,
                        )
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.onWriteClick = { singleEventHandler.throttleFirst { navigateToFeedWriting() } }
        setupAdapter()
        setupRefreshView()
    }

    private fun navigateToFeedWriting() {
        activityResultCallback.launch(CreateFeedActivity.getIntent(requireContext()))
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
                    feedViewModel.updateRefreshedFeeds(true)
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

        feedViewModel.categories.observe(viewLifecycleOwner) { category ->
            if (binding.wcgFeed.children.toList().isEmpty()) category.setUpChips()

            val selectedCategory = category.find { it.isSelected } ?: category.first()

            binding.wcgFeed.children.forEach {
                val chip = it as Chip
                chip.isSelected = chip.text == selectedCategory.category.krTitle
            }

            feedViewModel.updateFeeds(true)
        }
    }

    private fun List<CategoryModel>.setUpChips() {
        forEach { categoryUiState ->
            WebsosoChip(requireContext()).apply {
                setWebsosoChipText(categoryUiState.category.krTitle)
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

    private fun updateFeeds(feedUiState: FeedUiState) {
        val feeds = feedUiState.feeds.map { Feed(it) }

        when (feedUiState.isLoadable) {
            true -> {
                feedAdapter.submitList(feeds + Loading)
                binding.clFeedNone.isVisible = false
            }

            false -> {
                when (feeds.isNotEmpty()) {
                    true -> {
                        feedAdapter.submitList(feeds + NoMore)
                        binding.clFeedNone.isVisible = false
                    }

                    false -> {
                        feedAdapter.submitList(emptyList())
                        binding.clFeedNone.isVisible = true
                    }
                }
            }
        }

        if (feedUiState.isRefreshed) {
            lifecycleScope.launch {
                delay(300)
                binding.rvFeed.smoothScrollToPosition(0)
            }
        }
    }

    override fun onDestroyView() {
        _popupBinding = null
        super.onDestroyView()
    }
}
