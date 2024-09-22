package com.teamwss.websoso.ui.main.feed.dialog

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R.layout
import com.teamwss.websoso.R.string.report_popup_menu_impertinence_comment
import com.teamwss.websoso.R.string.report_popup_menu_impertinence_feed
import com.teamwss.websoso.R.string.report_popup_menu_spoiling_comment
import com.teamwss.websoso.R.string.report_popup_menu_spoiling_feed
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.databinding.DialogReportPopupMenuBinding
import com.teamwss.websoso.ui.main.feed.FeedFragment.FeedDialogClickListener
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType.IMPERTINENCE_COMMENT
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType.IMPERTINENCE_FEED
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType.SPOILER_COMMENT
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType.SPOILER_FEED

class FeedReportDialogFragment :
    BaseDialogFragment<DialogReportPopupMenuBinding>(layout.dialog_report_popup_menu) {
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val menuType: String? by lazy { arguments?.getString(MENU_TYPE) }
    private val onReportClick: FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedDialogClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (menuType) {
            SPOILER_COMMENT.name -> setupSpoilerCommentView()
            IMPERTINENCE_COMMENT.name -> setupImpertinenceCommentView()
            SPOILER_FEED.name -> setupSpoilerFeedView()
            IMPERTINENCE_FEED.name -> setupImpertinenceFeedView()
        }

        binding.tvReportPopupMenuCancel.setOnClickListener { dismiss() }
        dialog?.setCanceledOnTouchOutside(false)
    }

    private fun setupSpoilerCommentView() {
        binding.tvReportPopupMenuTitle.text = getString(report_popup_menu_spoiling_comment)
        binding.tvReportPopupMenuReport.setOnClickListener {
            singleEventHandler.throttleFirst {
                onReportClick()
                FeedReportDoneDialogFragment
                    .newInstance(IMPERTINENCE_COMMENT.name) { dismiss() }
                    .show(childFragmentManager, FeedReportDoneDialogFragment.TAG)
            }
        }
    }

    private fun setupImpertinenceCommentView() {
        binding.tvReportPopupMenuTitle.text = getString(
            report_popup_menu_impertinence_comment
        )
        binding.tvReportPopupMenuReport.setOnClickListener {
            singleEventHandler.throttleFirst {
                onReportClick()
                FeedReportDoneDialogFragment
                    .newInstance(IMPERTINENCE_COMMENT.name) { dismiss() }
                    .show(childFragmentManager, FeedReportDoneDialogFragment.TAG)
            }
        }
    }

    private fun setupSpoilerFeedView() {
        binding.tvReportPopupMenuTitle.text = getString(report_popup_menu_spoiling_feed)
        binding.tvReportPopupMenuReport.setOnClickListener {
            singleEventHandler.throttleFirst {
                onReportClick()
                FeedReportDoneDialogFragment
                    .newInstance(SPOILER_FEED.name) { dismiss() }
                    .show(childFragmentManager, FeedReportDoneDialogFragment.TAG)
            }
        }
    }

    private fun setupImpertinenceFeedView() {
        binding.tvReportPopupMenuTitle.text = getString(report_popup_menu_impertinence_feed)
        binding.tvReportPopupMenuReport.setOnClickListener {
            singleEventHandler.throttleFirst {
                onReportClick()
                FeedReportDoneDialogFragment
                    .newInstance(IMPERTINENCE_FEED.name) { dismiss() }
                    .show(childFragmentManager, FeedReportDoneDialogFragment.TAG)
            }
        }
    }

    companion object {
        private const val EVENT = "EVENT"
        private const val MENU_TYPE = "MENU_TYPE"
        const val TAG = "FeedReportDialogFragment"

        fun newInstance(
            menuType: String,
            event: FeedDialogClickListener,
        ): FeedReportDialogFragment =
            FeedReportDialogFragment().also {
                it.arguments = Bundle().apply {
                    putString(MENU_TYPE, menuType)
                    putSerializable(EVENT, event)
                }
            }
    }
}

enum class ReportMenuType {
    SPOILER_COMMENT,
    IMPERTINENCE_COMMENT,
    SPOILER_FEED,
    IMPERTINENCE_FEED,
}
