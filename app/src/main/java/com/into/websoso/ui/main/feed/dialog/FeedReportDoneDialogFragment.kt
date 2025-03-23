package com.into.websoso.ui.main.feed.dialog

import android.os.Bundle
import android.view.View
import com.into.websoso.R.layout.dialog_report_done_popup_menu
import com.into.websoso.core.common.ui.base.BaseDialogFragment
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.databinding.DialogReportDonePopupMenuBinding
import com.into.websoso.resource.R.string.report_popup_menu_description
import com.into.websoso.resource.R.string.report_popup_menu_description_comment
import com.into.websoso.ui.main.feed.FeedFragment.FeedDialogClickListener
import com.into.websoso.ui.main.feed.dialog.ReportMenuType.IMPERTINENCE_COMMENT
import com.into.websoso.ui.main.feed.dialog.ReportMenuType.IMPERTINENCE_FEED

class FeedReportDoneDialogFragment :
    BaseDialogFragment<DialogReportDonePopupMenuBinding>(dialog_report_done_popup_menu) {
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val menuType: String? by lazy { arguments?.getString(MENU_TYPE) }
    private val onCheckClick: FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedDialogClickListener
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        when (menuType) {
            IMPERTINENCE_COMMENT.name -> setupImpertinenceCommentView()
            IMPERTINENCE_FEED.name -> setupImpertinenceFeedView()
        }

        binding.tvReportPopupMenuCheck.setOnClickListener {
            singleEventHandler.throttleFirst {
                onCheckClick()
                dismiss()
            }
        }
        dialog?.setCanceledOnTouchOutside(false)
    }

    private fun setupImpertinenceFeedView() {
        binding.tvReportPopupMenuDescription.visibility = View.VISIBLE
        binding.tvReportPopupMenuDescription.text = getString(report_popup_menu_description)
    }

    private fun setupImpertinenceCommentView() {
        binding.tvReportPopupMenuDescription.visibility = View.VISIBLE
        binding.tvReportPopupMenuDescription.text =
            getString(report_popup_menu_description_comment)
    }

    companion object {
        private const val EVENT = "EVENT"
        private const val MENU_TYPE = "MENU_TYPE"
        const val TAG = "FeedReportDoneDialogFragment"

        fun newInstance(
            menuType: String,
            event: FeedDialogClickListener,
        ): FeedReportDoneDialogFragment =
            FeedReportDoneDialogFragment().also {
                it.arguments = Bundle().apply {
                    putString(MENU_TYPE, menuType)
                    putSerializable(EVENT, event)
                }
            }
    }
}
