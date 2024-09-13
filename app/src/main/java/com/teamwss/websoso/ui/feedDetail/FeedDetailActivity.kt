package com.teamwss.websoso.ui.feedDetail

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import coil.load
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.common.util.toIntPxFromDp
import com.teamwss.websoso.databinding.ActivityFeedDetailBinding
import com.teamwss.websoso.databinding.DialogRemovePopupMenuBinding
import com.teamwss.websoso.databinding.DialogReportPopupMenuBinding
import com.teamwss.websoso.databinding.MenuFeedPopupBinding
import com.teamwss.websoso.ui.createFeed.CreateFeedActivity
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity.MenuType.COMMENT
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity.MenuType.FEED
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailAdapter
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.Comment
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.Header
import com.teamwss.websoso.ui.feedDetail.model.EditFeedModel
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState
import com.teamwss.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_COMMENT
import com.teamwss.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_FEED
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType.IMPERTINENCE_COMMENT
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType.IMPERTINENCE_FEED
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType.SPOILER_COMMENT
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType.SPOILER_FEED
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedDetailActivity : BaseActivity<ActivityFeedDetailBinding>(R.layout.activity_feed_detail) {
    private enum class MenuType { COMMENT, FEED }

    private val feedId: Long by lazy { intent.getLongExtra(FEED_ID, DEFAULT_FEED_ID) }
    private val feedDetailViewModel: FeedDetailViewModel by viewModels()
    private val feedDetailAdapter: FeedDetailAdapter by lazy {
        FeedDetailAdapter(
            onFeedContentClick(),
            onCommentClick()
        )
    }
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private lateinit var activityResultCallback: ActivityResultLauncher<Intent>
    private val popupBinding: MenuFeedPopupBinding by lazy {
        MenuFeedPopupBinding.inflate(LayoutInflater.from(this))
    }
    private val popupMenu: PopupWindow by lazy {
        PopupWindow(
            popupBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true,
        ).apply { elevation = 2f }
    }

    private fun onFeedContentClick(): FeedDetailClickListener = object : FeedDetailClickListener {
        @SuppressLint("CutPasteId")
        override fun onLikeButtonClick(view: View, feedId: Long) {
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
                feedDetailViewModel.updateLike(view.isSelected, updatedLikeCount)
            }
        }

        override fun onNovelInfoClick(novelId: Long) {
            NovelDetailActivity.getIntent(this@FeedDetailActivity, novelId)
        }

        override fun onProfileClick(userId: Long, isMyFeed: Boolean) {
            // if (isMyFeed) 마이페이지 else 프로필 뷰
        }
    }

    private fun onCommentClick(): CommentClickListener = object : CommentClickListener {
        override fun onProfileClick(userId: Long, isMyComment: Boolean) {
            // if (isMyComment) 마이페이지 else 프로필 뷰
        }

        override fun onMoreButtonClick(view: View, commentId: Long, isMyComment: Boolean) {
            popupMenu.showAsDropDown(view)
            bindMenuByIsMine(commentId, isMyComment, COMMENT)
        }
    }

    private fun bindMenuByIsMine(id: Long, isMine: Boolean, menuType: MenuType) {
        when (isMine) {
            true -> popupBinding.setupMineByMenuType(id, menuType)
            false -> popupBinding.setupNotMineByMenuType(id, menuType)
        }
    }

    private fun MenuFeedPopupBinding.setupMineByMenuType(id: Long, menuType: MenuType) {
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
        menuContentTitle = getString(R.string.feed_popup_menu_content_isMyFeed).split(",")
        tvFeedPopupFirstItem.isSelected = true
        tvFeedPopupSecondItem.isSelected = true
    }

    private fun setupEditingFeed() {
        val feedContent =
            feedDetailViewModel.feedDetailUiState.value?.feed?.let { feed ->
                EditFeedModel(
                    feedId = feed.id,
                    novelId = feed.novel.id,
                    novelTitle = feed.novel.title,
                    feedContent = feed.content,
                    feedCategory = feed.relevantCategories,
                )
            } ?: throw IllegalArgumentException()

        activityResultCallback.launch(CreateFeedActivity.getIntent(this, feedContent))
    }

    private fun setupEditingComment(commentId: Long) {
        val writtenComment = feedDetailViewModel.feedDetailUiState.value?.comments?.find {
            it.commentId == commentId
        }?.commentContent.orEmpty()

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

    private fun MenuFeedPopupBinding.setupNotMineByMenuType(id: Long, menuType: MenuType) {
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
        menuContentTitle = getString(R.string.feed_popup_menu_content_report_isNotMyFeed).split(",")
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
            event = { feedDetailViewModel.updateReportedSpoilerComment(commentId) },
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
            event = { feedDetailViewModel.updateReportedImpertinenceComment(commentId) },
        )
    }

    private inline fun <reified Dialog : ViewDataBinding> showDialog(
        menuType: String? = null,
        noinline event: () -> Unit,
    ) {
        when (Dialog::class) {
            DialogRemovePopupMenuBinding::class -> FeedRemoveDialogFragment.newInstance(
                menuType = menuType ?: throw IllegalArgumentException(),
                event = { event() },
            ).show(supportFragmentManager, FeedRemoveDialogFragment.TAG)

            DialogReportPopupMenuBinding::class -> FeedReportDialogFragment.newInstance(
                menuType = menuType ?: throw IllegalArgumentException(),
                event = { event() },
            ).show(supportFragmentManager, FeedReportDialogFragment.TAG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityResultCallback = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == REFRESH) feedDetailViewModel.updateFeedDetail(feedId)
        }
        setupView()
        setupObserver()
        onFeedDetailClick()
    }

    private fun onFeedDetailClick() {
        binding.ivFeedDetailBackButton.setOnClickListener {
            setResult(RESULT_OK)
            if (!isFinishing) finish()
        }

        binding.ivFeedDetailMoreButton.setOnClickListener {
            val isMyFeed = feedDetailViewModel.feedDetailUiState.value?.feed?.isMyFeed
                ?: throw IllegalStateException()

            popupMenu.showAsDropDown(binding.ivFeedDetailMoreButton)
            bindMenuByIsMine(feedId, isMyFeed, FEED)
        }

        binding.ivFeedDetailCommentRegister.setOnClickListener {
            binding.etFeedDetailInput.run {
                when (feedDetailViewModel.commentId == DEFAULT_FEED_ID) {
                    true -> feedDetailViewModel.dispatchComment(text.toString())
                    false -> feedDetailViewModel.modifyComment(text.toString())
                }
                text.clear()
                clearFocus()
            }
        }
    }

    private fun setupView() {
        setupRefreshView()
        feedDetailViewModel.updateFeedDetail(feedId)
        binding.rvFeedDetail.apply {
            adapter = feedDetailAdapter
            itemAnimator = null
        }
        binding.ivFeedDetailMyProfileImage.load(
            feedDetailViewModel.feedDetailUiState.value?.feed?.user?.avatarImage
        )
    }

    private fun setupRefreshView() {
        binding.sptrFeedRefresh.apply {
            setRefreshViewParams(ViewGroup.LayoutParams(30.toIntPxFromDp(), 30.toIntPxFromDp()))
            setLottieAnimation(LOTTIE_IMAGE)
            setOnRefreshListener { feedDetailViewModel.updateFeedDetail(feedId) }
        }
    }

    private fun setupObserver() {
        feedDetailViewModel.feedDetailUiState.observe(this) { feedDetailUiState ->
            when {
                feedDetailUiState.loading -> binding.wllFeed.setWebsosoLoadingVisibility(true)
                feedDetailUiState.error -> {
                    binding.wllFeed.setLoadingLayoutVisibility(false)
                    setResult(RESULT_FAIL)
                    if (!isFinishing) finish()
                }

                !feedDetailUiState.loading -> {
                    binding.wllFeed.setWebsosoLoadingVisibility(false)
                    binding.sptrFeedRefresh.setRefreshing(false)
                    updateView(feedDetailUiState)
                }
            }
        }
    }

    private fun updateView(feedDetailUiState: FeedDetailUiState) {
        val header = feedDetailUiState.feed?.let { Header(it) }
        val comments = feedDetailUiState.comments.map { Comment(it) }
        val feedDetail = listOf(header) + comments

        with(feedDetailAdapter) {
            when (itemCount == 0) {
                true -> submitList(feedDetail)
                false -> submitList(feedDetail).also {
                    binding.rvFeedDetail.smoothScrollToPosition(itemCount)
                }
            }
        }
    }

    companion object {
        private const val FEED_ID: String = "FEED_ID"
        private const val DEFAULT_FEED_ID: Long = -1
        private const val LOTTIE_IMAGE = "lottie_websoso_loading.json"
        private const val REFRESH = 200
        private const val RESULT_OK = 200
        private const val RESULT_FAIL = 400

        fun getIntent(context: Context, feedId: Long): Intent =
            Intent(context, FeedDetailActivity::class.java).apply { putExtra(FEED_ID, feedId) }
    }
}
