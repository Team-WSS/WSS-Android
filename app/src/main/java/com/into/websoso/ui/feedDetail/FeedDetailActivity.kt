package com.into.websoso.ui.feedDetail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.RoundedCornersTransformation
import com.into.websoso.R.id.tv_feed_thumb_up_count
import com.into.websoso.R.layout.activity_feed_detail
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.model.ResultFrom.BlockUser
import com.into.websoso.core.common.ui.model.ResultFrom.CreateFeed
import com.into.websoso.core.common.ui.model.ResultFrom.Feed
import com.into.websoso.core.common.ui.model.ResultFrom.FeedDetailBack
import com.into.websoso.core.common.ui.model.ResultFrom.FeedDetailError
import com.into.websoso.core.common.ui.model.ResultFrom.FeedDetailRefreshed
import com.into.websoso.core.common.ui.model.ResultFrom.FeedDetailRemoved
import com.into.websoso.core.common.ui.model.ResultFrom.NovelDetailBack
import com.into.websoso.core.common.ui.model.ResultFrom.OtherUserProfileBack
import com.into.websoso.core.common.ui.model.ResultFrom.WithdrawUser
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.core.common.util.hideKeyboard
import com.into.websoso.core.common.util.showWebsosoSnackBar
import com.into.websoso.core.common.util.toFloatPxFromDp
import com.into.websoso.core.common.util.toIntPxFromDp
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.resource.R.drawable.ic_blocked_user_snack_bar
import com.into.websoso.core.resource.R.string.feed_popup_menu_content_isMyFeed
import com.into.websoso.core.resource.R.string.feed_popup_menu_content_report_isNotMyFeed
import com.into.websoso.core.resource.R.string.other_user_page_withdraw_user
import com.into.websoso.databinding.ActivityFeedDetailBinding
import com.into.websoso.databinding.DialogRemovePopupMenuBinding
import com.into.websoso.databinding.DialogReportPopupMenuBinding
import com.into.websoso.databinding.MenuFeedPopupBinding
import com.into.websoso.ui.createFeed.CreateFeedActivity
import com.into.websoso.ui.feedDetail.FeedDetailActivity.MenuType.COMMENT
import com.into.websoso.ui.feedDetail.FeedDetailActivity.MenuType.FEED
import com.into.websoso.ui.feedDetail.FeedDetailViewModel.Companion.DEFAULT_NOTIFICATION_ID
import com.into.websoso.ui.feedDetail.adapter.FeedDetailAdapter
import com.into.websoso.ui.feedDetail.adapter.FeedDetailType.Comment
import com.into.websoso.ui.feedDetail.adapter.FeedDetailType.Header
import com.into.websoso.ui.feedDetail.dialog.RemovedFeedDialogFragment
import com.into.websoso.ui.feedDetail.model.EditFeedModel
import com.into.websoso.ui.feedDetail.model.FeedDetailUiState
import com.into.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.into.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.into.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_COMMENT
import com.into.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_FEED
import com.into.websoso.ui.main.feed.dialog.ReportMenuType.IMPERTINENCE_COMMENT
import com.into.websoso.ui.main.feed.dialog.ReportMenuType.IMPERTINENCE_FEED
import com.into.websoso.ui.main.feed.dialog.ReportMenuType.SPOILER_COMMENT
import com.into.websoso.ui.main.feed.dialog.ReportMenuType.SPOILER_FEED
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.into.websoso.ui.otherUserPage.BlockUserDialogFragment.Companion.USER_NICKNAME
import com.into.websoso.ui.otherUserPage.OtherUserPageActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedDetailActivity : BaseActivity<ActivityFeedDetailBinding>(activity_feed_detail) {
    @Inject
    lateinit var tracker: Tracker

    private enum class MenuType { COMMENT, FEED }

    private val feedId: Long by lazy { intent.getLongExtra(FEED_ID, DEFAULT_FEED_ID) }
    private val feedDetailViewModel: FeedDetailViewModel by viewModels()
    private val feedDetailAdapter: FeedDetailAdapter by lazy {
        FeedDetailAdapter(
            onFeedContentClick(),
            onCommentClick(),
        )
    }
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private lateinit var activityResultCallback: ActivityResultLauncher<Intent>
    private val popupBinding: MenuFeedPopupBinding by lazy {
        MenuFeedPopupBinding.inflate(LayoutInflater.from(this))
    }
    private val popupMenu: PopupWindow by lazy {
        PopupWindow(popupBinding.root, WRAP_CONTENT, WRAP_CONTENT, true).apply { elevation = 2f }
    }

    private fun onFeedContentClick(): FeedDetailClickListener =
        object : FeedDetailClickListener {
            @SuppressLint("CutPasteId")
            override fun onLikeButtonClick(
                view: View,
                feedId: Long,
            ) {
                val likeCount: Int =
                    view
                        .findViewById<TextView>(tv_feed_thumb_up_count)
                        .text
                        .toString()
                        .toInt()
                val updatedLikeCount: Int = when (view.isSelected) {
                    true -> if (likeCount > 0) likeCount - 1 else 0
                    false -> likeCount + 1
                }

                view.findViewById<TextView>(tv_feed_thumb_up_count).text =
                    updatedLikeCount.toString()
                view.isSelected = !view.isSelected

                singleEventHandler.debounce(coroutineScope = lifecycleScope) {
                    tracker.trackEvent("feed_detail_like")
                    feedDetailViewModel.updateLike(view.isSelected, updatedLikeCount)
                }
            }

            override fun onNovelInfoClick(novelId: Long) {
                navigateToNovelDetail(novelId)
            }

            override fun onProfileClick(
                userId: Long,
                isMyFeed: Boolean,
            ) {
                if (isMyFeed) return
                navigateToProfile(userId)
            }

            override fun onFeedDetailClick(view: View) {
                view.hideKeyboard()
            }

            override fun onFeedImageClick(
                imageUrls: List<String>,
                index: Int,
            ) {
                navigateToExpandedImage(imageUrls, index)
            }
        }

    private fun navigateToNovelDetail(novelId: Long) {
        activityResultCallback.launch(
            NovelDetailActivity.getIntent(this@FeedDetailActivity, novelId),
        )
    }

    private fun onCommentClick(): CommentClickListener =
        object : CommentClickListener {
            override fun onProfileClick(
                userId: Long,
                isMyComment: Boolean,
            ) {
                if (isMyComment) return
                navigateToProfile(userId)
            }

            override fun onMoreButtonClick(
                view: View,
                commentId: Long,
                isMyComment: Boolean,
            ) {
                popupMenu.showAsDropDown(view)
                bindMenuByIsMine(commentId, isMyComment, COMMENT)
            }

            override fun onCommentsClick(view: View) {
                view.hideKeyboard()
            }
        }

    private fun navigateToProfile(userId: Long) {
        activityResultCallback.launch(
            OtherUserPageActivity.getIntent(this@FeedDetailActivity, userId),
        )
    }

    private fun navigateToExpandedImage(
        imageUrls: List<String>,
        index: Int,
    ) {
        // TODO("Not yet implemented")
    }

    private fun bindMenuByIsMine(
        id: Long,
        isMine: Boolean,
        menuType: MenuType,
    ) {
        when (isMine) {
            true -> popupBinding.setupMineByMenuType(id, menuType)
            false -> popupBinding.setupNotMineByMenuType(id, menuType)
        }
    }

    private fun MenuFeedPopupBinding.setupMineByMenuType(
        id: Long,
        menuType: MenuType,
    ) {
        onFirstItemClick = {
            when (menuType) {
                FEED -> setupEditingFeed()
                COMMENT -> setupEditingComment(id)
            }
            popupMenu.dismiss()
        }
        onSecondItemClick = {
            when (menuType) {
                FEED -> setupRemovingFeed()
                COMMENT -> setupRemovingComment(id)
            }
            popupMenu.dismiss()
        }
        menuContentTitle = getString(feed_popup_menu_content_isMyFeed).split(",")
        tvFeedPopupFirstItem.isSelected = true
        tvFeedPopupSecondItem.isSelected = true
    }

    private fun setupEditingFeed() {
        val feedContent =
            feedDetailViewModel.feedDetailUiState.value?.feedDetail?.feed?.let { feed ->
                EditFeedModel(
                    feedId = feed.id,
                    novelId = feed.novel.id,
                    novelTitle = feed.novel.title,
                    isSpoiler = feed.isSpoiler,
                    isPublic = feed.isPublic,
                    feedContent = feed.content,
                    feedCategory = feed.relevantCategories,
                )
            } ?: throw IllegalArgumentException()

        activityResultCallback.launch(CreateFeedActivity.getIntent(this, feedContent))
    }

    private fun setupEditingComment(commentId: Long) {
        val writtenComment =
            feedDetailViewModel.feedDetailUiState.value
                ?.feedDetail
                ?.comments
                ?.find {
                    it.commentId == commentId
                }?.commentContent
                .orEmpty()

        feedDetailViewModel.updateCommentId(commentId)
        binding.etFeedDetailInput.setText(writtenComment)
        binding.etFeedDetailInput.requestFocus()
    }

    private fun setupRemovingFeed() {
        showDialog<DialogRemovePopupMenuBinding>(
            menuType = REMOVE_FEED.name,
            event = { feedDetailViewModel.updateRemovedFeed().also { finish() } },
        )
    }

    private fun setupRemovingComment(commentId: Long) {
        showDialog<DialogRemovePopupMenuBinding>(
            menuType = REMOVE_COMMENT.name,
            event = { feedDetailViewModel.updateRemovedComment(commentId) },
        )
    }

    private fun MenuFeedPopupBinding.setupNotMineByMenuType(
        id: Long,
        menuType: MenuType,
    ) {
        onFirstItemClick = {
            when (menuType) {
                FEED -> setupReportingSpoilerFeed()
                COMMENT -> setupReportingSpoilerComment(id)
            }
            popupMenu.dismiss()
        }
        onSecondItemClick = {
            when (menuType) {
                FEED -> setupReportingImpertinenceFeed()
                COMMENT -> setupReportingImpertinenceComment(id)
            }
            popupMenu.dismiss()
        }
        menuContentTitle = getString(feed_popup_menu_content_report_isNotMyFeed).split(",")
        tvFeedPopupFirstItem.isSelected = false
        tvFeedPopupSecondItem.isSelected = false
    }

    private fun setupReportingSpoilerFeed() {
        showDialog<DialogReportPopupMenuBinding>(
            menuType = SPOILER_FEED.name,
            event = { feedDetailViewModel.updateReportedSpoilerFeed() },
        )
    }

    private fun setupReportingSpoilerComment(commentId: Long) {
        showDialog<DialogReportPopupMenuBinding>(
            menuType = SPOILER_COMMENT.name,
            event = {
                tracker.trackEvent("alert_comment_spoiler")
                feedDetailViewModel.updateReportedSpoilerComment(commentId)
            },
        )
    }

    private fun setupReportingImpertinenceFeed() {
        showDialog<DialogReportPopupMenuBinding>(
            menuType = IMPERTINENCE_FEED.name,
            event = { feedDetailViewModel.updateReportedImpertinenceFeed() },
        )
    }

    private fun setupReportingImpertinenceComment(commentId: Long) {
        showDialog<DialogReportPopupMenuBinding>(
            menuType = IMPERTINENCE_COMMENT.name,
            event = {
                tracker.trackEvent("alert_feed_abuse")
                feedDetailViewModel.updateReportedImpertinenceComment(commentId)
            },
        )
    }

    private inline fun <reified Dialog : ViewDataBinding> showDialog(
        menuType: String? = null,
        noinline event: () -> Unit,
    ) {
        when (Dialog::class) {
            DialogRemovePopupMenuBinding::class ->
                FeedRemoveDialogFragment
                    .newInstance(
                        menuType = menuType ?: throw IllegalArgumentException(),
                        event = { event() },
                    ).show(supportFragmentManager, FeedRemoveDialogFragment.TAG)

            DialogReportPopupMenuBinding::class ->
                FeedReportDialogFragment
                    .newInstance(
                        menuType = menuType ?: throw IllegalArgumentException(),
                        event = { event() },
                    ).show(supportFragmentManager, FeedReportDialogFragment.TAG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupObserver()
        onFeedDetailClick()
        refreshView()
        updateNotificationRead()
        tracker.trackEvent("feed_detail")
    }

    private fun refreshView() {
        if (::activityResultCallback.isInitialized.not()) {
            activityResultCallback = registerForActivityResult(StartActivityForResult()) { result ->
                when (result.resultCode) {
                    NovelDetailBack.RESULT_OK,
                    CreateFeed.RESULT_OK,
                    OtherUserProfileBack.RESULT_OK,
                    -> feedDetailViewModel.updateFeedDetail(feedId, CreateFeed)

                    BlockUser.RESULT_OK -> {
                        val nickname = result.data?.getStringExtra(USER_NICKNAME).orEmpty()
                        val intent = Intent().apply {
                            putExtra(USER_NICKNAME, nickname)
                            putExtra(FEED_ID, feedId)
                        }
                        setResult(BlockUser.RESULT_OK, intent)
                        if (!isFinishing) finish()
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
        }
    }

    private fun updateNotificationRead() {
        val notificationId = intent.getLongExtra(NOTIFICATION_ID, DEFAULT_NOTIFICATION_ID)
        feedDetailViewModel.updateNotificationRead(notificationId)
    }

    private fun onFeedDetailClick() {
        onBackPressedDispatcher.addCallback(this) {
            setResult(FeedDetailBack.RESULT_OK)
            if (!isFinishing) finish()
        }

        binding.root.setOnClickListener { it.hideKeyboard() }

        binding.ivFeedDetailBackButton.setOnClickListener {
            val intent = Intent().apply {
                putExtra(FEED_ID, feedId)
                putExtra(
                    FEED_LIKE_STATUS,
                    feedDetailViewModel.feedDetailUiState.value
                        ?.feedDetail
                        ?.feed
                        ?.isLiked,
                )
                putExtra(
                    FEED_LIKE_COUNT,
                    feedDetailViewModel.feedDetailUiState.value
                        ?.feedDetail
                        ?.feed
                        ?.likeCount,
                )
            }
            setResult(FeedDetailBack.RESULT_OK, intent)
            if (!isFinishing) finish()
        }

        binding.ivFeedDetailMoreButton.setOnClickListener {
            val isMyFeed = feedDetailViewModel.feedDetailUiState.value
                ?.feedDetail
                ?.feed
                ?.isMyFeed
                ?: throw IllegalStateException()

            popupMenu.showAsDropDown(binding.ivFeedDetailMoreButton)
            bindMenuByIsMine(feedId, isMyFeed, FEED)
        }

        binding.ivFeedDetailCommentRegister.setOnClickListener {
            if (binding.etFeedDetailInput.text
                    .isNullOrBlank()
                    .not()
            ) {
                binding.etFeedDetailInput.run {
                    tracker.trackEvent("write_comment")
                    when (feedDetailViewModel.commentId == DEFAULT_FEED_ID) {
                        true -> feedDetailViewModel.dispatchComment(text.toString())
                        false -> feedDetailViewModel.modifyComment(text.toString())
                    }
                    text.clear()
                    clearFocus()
                }
                it.hideKeyboard()
            }
        }
    }

    private fun setupView() {
        setupRefreshView()
        feedDetailViewModel.updateFeedDetail(feedId, Feed)
        binding.rvFeedDetail.apply {
            adapter = feedDetailAdapter
            itemAnimator = null
        }
    }

    private fun setupRefreshView() {
        binding.sptrFeedRefresh.apply {
            setRefreshViewParams(LayoutParams(30.toIntPxFromDp(), 30.toIntPxFromDp()))
            setLottieAnimation(LOTTIE_IMAGE)
            setOnRefreshListener {
                feedDetailViewModel.updateFeedDetail(feedId, FeedDetailRefreshed)
            }
        }
    }

    private fun setupObserver() {
        feedDetailViewModel.feedDetailUiState.observe(this) { feedDetailUiState ->
            when {
                feedDetailUiState.loading -> binding.wllFeed.setWebsosoLoadingVisibility(true)
                feedDetailUiState.error -> {
                    binding.wllFeed.setLoadingLayoutVisibility(false)

                    when (feedDetailUiState.previousStack.from) {
                        CreateFeed, FeedDetailRefreshed ->
                            RemovedFeedDialogFragment
                                .newInstance {
                                    val extraIntent = Intent().apply { putExtra(FEED_ID, feedId) }
                                    setResult(FeedDetailRefreshed.RESULT_OK, extraIntent)
                                    if (!isFinishing) finish()
                                }.show(supportFragmentManager, RemovedFeedDialogFragment.TAG)

                        else -> {
                            val extraIntent = Intent().apply { putExtra(FEED_ID, feedId) }
                            setResult(FeedDetailRemoved.RESULT_OK, extraIntent)
                            if (!isFinishing) finish()
                        }
                    }
                }

                feedDetailUiState.isServerError -> {
                    val extraIntent = Intent().apply { putExtra(FEED_ID, feedId) }
                    setResult(FeedDetailError.RESULT_OK, extraIntent)
                    if (!isFinishing) finish()
                }

                !feedDetailUiState.loading -> {
                    binding.wllFeed.setWebsosoLoadingVisibility(false)
                    binding.sptrFeedRefresh.setRefreshing(false)
                    updateView(feedDetailUiState)
                }
            }
        }

        binding.etFeedDetailInput.addTextChangedListener(
            object : TextWatcher {
                override fun onTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int,
                ) {
                    when (p0?.trim().isNullOrBlank()) {
                        true -> binding.ivFeedDetailCommentRegister.isSelected = false
                        false -> binding.ivFeedDetailCommentRegister.isSelected = true
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) = Unit

                override fun afterTextChanged(s: Editable?) = Unit
            },
        )
    }

    private fun updateView(feedDetailUiState: FeedDetailUiState) {
        feedDetailUiState.feedDetail.user?.avatarImage?.let { image ->
            binding.ivFeedDetailMyProfileImage.apply {
                val scaledImage = getS3ImageUrl(image)
                load(scaledImage) {
                    transformations(RoundedCornersTransformation(14f.toFloatPxFromDp()))
                }
            }
        }

        val header = feedDetailUiState.feedDetail.feed?.let { Header(it) }
        val comments = feedDetailUiState.feedDetail.comments.map { Comment(it) }
        val feedDetail = listOf(header) + comments

        with(feedDetailAdapter) {
            submitList(feedDetail).also {
                when (feedDetailUiState.isRefreshed) {
                    true -> binding.rvFeedDetail.smoothScrollToPosition(0)
                    false -> binding.rvFeedDetail.smoothScrollToPosition(itemCount)
                }
            }
        }
    }

    companion object {
        const val FEED_ID: String = "FEED_ID"
        const val FEED_LIKE_STATUS: String = "FEED_LIKE_STATUS"
        const val FEED_LIKE_COUNT: String = "FEED_LIKE_COUNT"
        private const val DEFAULT_FEED_ID: Long = -1
        private const val NOTIFICATION_ID: String = "NOTIFICATION_ID"
        private const val LOTTIE_IMAGE = "lottie_websoso_loading.json"

        fun getIntent(
            context: Context,
            feedId: Long,
            notificationId: Long? = null,
        ): Intent =
            Intent(context, FeedDetailActivity::class.java).apply {
                putExtra(FEED_ID, feedId)
                putExtra(NOTIFICATION_ID, notificationId)
            }
    }
}
