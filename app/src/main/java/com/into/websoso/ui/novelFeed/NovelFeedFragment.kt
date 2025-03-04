package com.into.websoso.ui.novelFeed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.into.websoso.R
import com.into.websoso.R.drawable.ic_blocked_user_snack_bar
import com.into.websoso.R.drawable.ic_novel_detail_check
import com.into.websoso.R.string.block_user_success_message
import com.into.websoso.R.string.feed_popup_menu_content_isMyFeed
import com.into.websoso.R.string.feed_popup_menu_content_report_isNotMyFeed
import com.into.websoso.R.string.other_user_page_withdraw_user
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.ui.model.ResultFrom.BlockUser
import com.into.websoso.core.common.ui.model.ResultFrom.OtherUserProfileBack
import com.into.websoso.core.common.ui.model.ResultFrom.WithdrawUser
import com.into.websoso.core.common.util.InfiniteScrollListener
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.showWebsosoSnackBar
import com.into.websoso.core.common.util.toIntPxFromDp
import com.into.websoso.databinding.DialogRemovePopupMenuBinding
import com.into.websoso.databinding.DialogReportPopupMenuBinding
import com.into.websoso.databinding.FragmentNovelFeedBinding
import com.into.websoso.databinding.MenuFeedPopupBinding
import com.into.websoso.ui.common.dialog.LoginRequestDialogFragment
import com.into.websoso.ui.createFeed.CreateFeedActivity
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.feedDetail.model.EditFeedModel
import com.into.websoso.ui.main.feed.FeedItemClickListener
import com.into.websoso.ui.main.feed.adapter.FeedAdapter
import com.into.websoso.ui.main.feed.adapter.FeedType.Feed
import com.into.websoso.ui.main.feed.adapter.FeedType.Loading
import com.into.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.into.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.into.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_FEED
import com.into.websoso.ui.main.feed.dialog.ReportMenuType
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.into.websoso.ui.novelFeed.model.NovelFeedUiState
import com.into.websoso.ui.otherUserPage.BlockUserDialogFragment
import com.into.websoso.ui.otherUserPage.OtherUserPageActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelFeedFragment : BaseFragment<FragmentNovelFeedBinding>(R.layout.fragment_novel_feed) {
    private val novelId: Long by lazy { requireArguments().getLong(NOVEL_ID) }
    private var _popupBinding: MenuFeedPopupBinding? = null
    private val popupBinding: MenuFeedPopupBinding
        get() = _popupBinding ?: error("error: binding is null")
    private val novelFeedViewModel: NovelFeedViewModel by activityViewModels()
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

    private fun onClickFeedItem() =
        object : FeedItemClickListener {
            override fun onProfileClick(
                userId: Long,
                isMyFeed: Boolean,
            ) {
                if (isMyFeed) return

                activityResultCallback.launch(
                    OtherUserPageActivity.getIntent(
                        requireContext(),
                        userId,
                    ),
                )
            }

            override fun onMoreButtonClick(
                view: View,
                feedId: Long,
                isMyFeed: Boolean,
            ) {
                if (novelFeedViewModel.isLogin.value == false) {
                    showLoginRequestDialog()
                    return
                }
                showMenu(view, feedId, isMyFeed)
            }

            override fun onContentClick(feedId: Long) {
                navigateToFeedDetail(feedId)
            }

            override fun onNovelInfoClick(novelId: Long) {
                if (novelId == this@NovelFeedFragment.novelId) return
                startActivity(NovelDetailActivity.getIntent(requireContext(), novelId))
            }

            override fun onLikeButtonClick(
                view: View,
                id: Long,
            ) {
                if (novelFeedViewModel.isLogin.value == false) {
                    showLoginRequestDialog()
                    return
                }
                val likeTextView = view.requireViewById<TextView>(R.id.tv_feed_thumb_up_count)
                val likeCount: Int = likeTextView.text.toString().toInt()

                val updatedLikeCount: Int = when (view.isSelected) {
                    true -> if (likeCount > 0) likeCount - 1 else 0
                    false -> likeCount + 1
                }

                likeTextView.text = updatedLikeCount.toString()
                view.isSelected = !view.isSelected

                singleEventHandler.debounce(coroutineScope = lifecycleScope) {
                    novelFeedViewModel.updateLike(id, view.isSelected, updatedLikeCount)
                }
            }
        }

    private fun showLoginRequestDialog() {
        val dialog = LoginRequestDialogFragment.newInstance()
        dialog.show(childFragmentManager, LoginRequestDialogFragment.TAG)
    }

    private fun showMenu(
        view: View,
        feedId: Long,
        isMyFeed: Boolean,
    ) {
        val popupWindow: PopupWindow = PopupWindow(
            popupBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true,
        ).apply {
            elevation = 2f
            showAsDropDown(view)
        }

        bindMenuByIsMyFeed(popupWindow, isMyFeed, feedId)
    }

    private fun bindMenuByIsMyFeed(
        popup: PopupWindow,
        isMyFeed: Boolean,
        feedId: Long,
    ) {
        with(popupBinding) {
            when (isMyFeed) {
                true -> setupMyFeed(feedId, popup)
                false -> setupNotMyFeed(feedId, popup)
            }
        }
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
                menuType = REMOVE_FEED.name,
                event = { novelFeedViewModel.updateRemovedFeed(feedId) },
            )
            popup.dismiss()
        }
        menuContentTitle =
            getString(feed_popup_menu_content_isMyFeed).split(",")
        tvFeedPopupFirstItem.isSelected = true
        tvFeedPopupSecondItem.isSelected = true
    }

    private fun MenuFeedPopupBinding.setupNotMyFeed(
        feedId: Long,
        popup: PopupWindow,
    ) {
        onFirstItemClick = {
            showDialog<DialogReportPopupMenuBinding>(
                menuType = ReportMenuType.SPOILER_FEED.name,
                event = { novelFeedViewModel.updateReportedSpoilerFeed(feedId) },
            )
            popup.dismiss()
        }
        onSecondItemClick = {
            showDialog<DialogReportPopupMenuBinding>(
                menuType = ReportMenuType.IMPERTINENCE_FEED.name,
                event = { novelFeedViewModel.updateReportedImpertinenceFeed(feedId) },
            )
            popup.dismiss()
        }
        menuContentTitle =
            getString(feed_popup_menu_content_report_isNotMyFeed).split(",")
        tvFeedPopupFirstItem.isSelected = false
        tvFeedPopupSecondItem.isSelected = false
    }

    private inline fun <reified Dialog : ViewDataBinding> showDialog(
        menuType: String,
        noinline event: () -> Unit,
    ) {
        when (Dialog::class) {
            DialogRemovePopupMenuBinding::class ->
                FeedRemoveDialogFragment
                    .newInstance(
                        menuType = menuType,
                        event = { event() },
                    ).show(childFragmentManager, FeedRemoveDialogFragment.TAG)

            DialogReportPopupMenuBinding::class ->
                FeedReportDialogFragment
                    .newInstance(
                        menuType = menuType,
                        event = { event() },
                    ).show(childFragmentManager, FeedReportDialogFragment.TAG)
        }
    }

    private fun navigateToFeedEdit(feedId: Long) {
        val feedContent =
            novelFeedViewModel.feedUiState.value?.feeds?.find { it.id == feedId }?.let { feed ->
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
        if (novelFeedViewModel.isLogin.value == false) {
            showLoginRequestDialog()
            return
        }
        startActivity(FeedDetailActivity.getIntent(requireContext(), feedId))
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        activityResultCallback =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    OtherUserProfileBack.RESULT_OK -> novelFeedViewModel.updateRefreshedFeeds(
                        novelId,
                    )

                    BlockUser.RESULT_OK -> {
                        val nickname =
                            result.data?.getStringExtra(BlockUserDialogFragment.USER_NICKNAME)
                        val blockMessage = nickname?.let {
                            getString(block_user_success_message, it)
                        } ?: getString(block_user_success_message)

                        showWebsosoSnackBar(
                            view = binding.root,
                            message = blockMessage,
                            icon = ic_novel_detail_check,
                        )

                        novelFeedViewModel.updateRefreshedFeeds(novelId)
                    }

                    WithdrawUser.RESULT_OK -> {
                        showWebsosoSnackBar(
                            view = binding.root,
                            message = getString(other_user_page_withdraw_user),
                            icon = ic_blocked_user_snack_bar,
                        )
                    }
                }
            }
        initView()
        novelFeedViewModel.updateFeeds(novelId)
        novelFeedViewModel.updateLoginStatus()
        setupObserver()
    }

    private fun initView() {
        setupAdapter()
        setupRefreshView()
        setupBackgorundView()
    }

    private fun setupAdapter() {
        binding.rvNovelFeed.apply {
            adapter = feedAdapter
            itemAnimator = null
            addOnScrollListener(
                InfiniteScrollListener.of(
                    singleEventHandler = singleEventHandler,
                    event = { novelFeedViewModel.updateFeeds(novelId) },
                ),
            )
            setHasFixedSize(true)
        }
    }

    private fun setupRefreshView() {
        binding.sptrNovelFeedRefresh.apply {
            setRefreshViewParams(
                params = ViewGroup.LayoutParams(
                    30.toIntPxFromDp(),
                    30.toIntPxFromDp(),
                ),
            )
            setLottieAnimation("lottie_websoso_loading.json")
            setOnRefreshListener {
                novelFeedViewModel.updateRefreshedFeeds(novelId)
            }
        }
    }

    private fun setupBackgorundView() {
        val backgroundView = binding.viewNovelFeedBackground
        val layoutParams = backgroundView.layoutParams
        layoutParams.height = (resources.displayMetrics.heightPixels - 54.toIntPxFromDp())
        backgroundView.layoutParams = layoutParams
    }

    private fun setupObserver() {
        novelFeedViewModel.feedUiState.observe(viewLifecycleOwner) { novelFeedUiState ->
            when {
                novelFeedUiState.loading -> binding.wllNovelFeed.setWebsosoLoadingVisibility(true)
                novelFeedUiState.error -> binding.wllNovelFeed.setLoadingLayoutVisibility(false)
                !novelFeedUiState.loading -> {
                    binding.wllNovelFeed.setWebsosoLoadingVisibility(false)
                    binding.sptrNovelFeedRefresh.setRefreshing(false)
                    updateFeeds(novelFeedUiState)
                }
            }
        }
        novelFeedViewModel.isRefreshed.observe(viewLifecycleOwner) { isRefreshed ->
            if (isRefreshed) {
                novelFeedViewModel.updateIsRefreshed(false)
                binding.rvNovelFeed.scrollToPosition(0)
            }
        }
    }

    private fun updateFeeds(novelFeedUiState: NovelFeedUiState) {
        binding.clNovelFeedNone.isVisible = novelFeedUiState.feeds.isEmpty()
        val feeds = novelFeedUiState.feeds.map { Feed(it) }
        when (novelFeedUiState.isLoadable) {
            true -> feedAdapter.submitList(feeds + Loading)
            false -> feedAdapter.submitList(feeds)
        }
    }

    override fun onResume() {
        super.onResume()
        if (novelFeedViewModel.feedUiState.value
                ?.feeds
                .isNullOrEmpty()
                .not()
        ) {
            novelFeedViewModel.updateRefreshedFeeds(novelId)
        }
    }

    override fun onDestroyView() {
        _popupBinding = null
        super.onDestroyView()
    }

    companion object {
        private const val NOVEL_ID = "NOVEL_ID"

        fun newInstance(novelId: Long): NovelFeedFragment =
            NovelFeedFragment().also {
                it.arguments = Bundle().apply { putLong(NOVEL_ID, novelId) }
            }
    }
}
