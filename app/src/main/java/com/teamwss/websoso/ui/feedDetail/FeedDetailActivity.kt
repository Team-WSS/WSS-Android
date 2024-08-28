package com.teamwss.websoso.ui.feedDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityFeedDetailBinding
import com.teamwss.websoso.databinding.DialogRemovePopupMenuBinding
import com.teamwss.websoso.databinding.DialogReportPopupMenuBinding
import com.teamwss.websoso.databinding.MenuFeedPopupBinding
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailAdapter
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.Comment
import com.teamwss.websoso.ui.feedDetail.adapter.FeedDetailType.Header
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Error
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Loading
import com.teamwss.websoso.ui.feedDetail.model.FeedDetailUiState.Success
import com.teamwss.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_COMMENT
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType.IMPERTINENCE_COMMENT
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType.SPOILER_COMMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedDetailActivity : BaseActivity<ActivityFeedDetailBinding>(R.layout.activity_feed_detail) {
    private val feedDetailViewModel: FeedDetailViewModel by viewModels()
    private val feedId: Long by lazy { intent.getLongExtra(FEED_ID, DEFAULT_FEED_ID) }
    private val feedDetailAdapter: FeedDetailAdapter by lazy { FeedDetailAdapter(onCommentClick()) }
    private val popupBinding: MenuFeedPopupBinding by lazy {
        MenuFeedPopupBinding.inflate(LayoutInflater.from(this))
    }

    private fun onCommentClick(): CommentClickListener = object : CommentClickListener {
        override fun onProfileClick(userId: Long, isMyComment: Boolean) {
            // if (isMyComment) 마이페이지 else 프로필 뷰
        }

        override fun onMoreButtonClick(view: View, commentId: Long, isMyComment: Boolean) {
            showMenu(view, commentId, isMyComment)
        }
    }

    private fun showMenu(view: View, commentId: Long, isMyComment: Boolean) {
        val popupWindow: PopupWindow = PopupWindow(
            popupBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 2f
            showAsDropDown(view)
        }

        bindMenuByIsMyComment(popupWindow, isMyComment, commentId)
    }

    private fun bindMenuByIsMyComment(popup: PopupWindow, isMyComment: Boolean, commentId: Long) {
        with(popupBinding) {
            when (isMyComment) {
                true -> setupMyComment(commentId, popup)
                false -> setupNotMyComment(commentId, popup)
            }
        }
    }

    private fun MenuFeedPopupBinding.setupMyComment(commentId: Long, popup: PopupWindow) {
        onFirstItemClick = {
            val writtenComment = (feedDetailViewModel.feedDetailUiState.value as Success)
                .feedDetail
                .comments
                .find { it.commentId == commentId }
                ?.commentContent ?: ""

            feedDetailViewModel.updateCommentId(commentId)
            binding.etFeedDetailInput.setText(writtenComment)
            binding.etFeedDetailInput.requestFocus()
            popup.dismiss()
        }
        onSecondItemClick = {
            showDialog<DialogRemovePopupMenuBinding>(
                menuType = REMOVE_COMMENT.name,
                event = { feedDetailViewModel.updateRemovedComment(commentId) },
            )
            popup.dismiss()
        }
        menuContentTitle = getString(R.string.feed_popup_menu_content_isMyFeed).split(",")
        tvFeedPopupFirstItem.isSelected = true
        tvFeedPopupSecondItem.isSelected = true
    }

    private fun MenuFeedPopupBinding.setupNotMyComment(commentId: Long, popup: PopupWindow) {
        onFirstItemClick = {
            showDialog<DialogReportPopupMenuBinding>(
                menuType = SPOILER_COMMENT.name,
                event = { feedDetailViewModel.updateReportedSpoilerComment(commentId) },
            )
            popup.dismiss()
        }
        onSecondItemClick = {
            showDialog<DialogReportPopupMenuBinding>(
                menuType = IMPERTINENCE_COMMENT.name,
                event = { feedDetailViewModel.updateReportedImpertinenceComment(commentId) },
            )
            popup.dismiss()
        }
        menuContentTitle = getString(R.string.feed_popup_menu_content_report_isNotMyFeed).split(",")
        tvFeedPopupFirstItem.isSelected = false
        tvFeedPopupSecondItem.isSelected = false
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

        setupView()
        setupObserver()
        onCommentRegisterClick()
    }

    private fun setupView() {
        feedDetailViewModel.updateFeedDetail(feedId)
        binding.rvFeedDetail.apply {
            adapter = feedDetailAdapter
            itemAnimator = null
        }
    }

    private fun setupObserver() {
        feedDetailViewModel.feedDetailUiState.observe(this) { feedDetailUiState ->
            when (feedDetailUiState) {
                is Success -> updateView(feedDetailUiState)
                is Loading -> {}
                is Error -> {}
            }
        }
    }

    private fun updateView(feedDetailUiState: Success) {
        val comments = feedDetailUiState.feedDetail.comments.map { Comment(it) }
        val header = Header(feedDetailUiState.feedDetail.feed.copy(commentCount = comments.size))

        feedDetailAdapter.submitList(listOf(header) + comments)
    }

    private fun onCommentRegisterClick() {
        binding.ivFeedDetailCommentRegister.setOnClickListener {
            binding.etFeedDetailInput.run {
                when (feedDetailViewModel.commentId == DEFAULT_FEED_ID) {
                    true -> feedDetailViewModel.dispatchComment(feedId, text.toString())
                    false -> feedDetailViewModel.modifyComment(feedId, text.toString())
                }
                text.clear()
                clearFocus()
            }
        }
    }

    companion object {
        private const val FEED_ID: String = "FEED_ID"
        private const val DEFAULT_FEED_ID: Long = -1

        fun getIntent(context: Context, feedId: Long): Intent =
            Intent(context, FeedDetailActivity::class.java).apply { putExtra(FEED_ID, feedId) }
    }
}
