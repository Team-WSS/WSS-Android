package com.into.websoso.ui.createFeed

import android.os.Bundle
import android.view.View
import com.into.websoso.R.layout.dialog_report_popup_menu
import com.into.websoso.core.common.ui.base.BaseDialogFragment
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.resource.R.string.remove_popup_menu_keep_creating
import com.into.websoso.core.resource.R.string.remove_popup_menu_stop_creating
import com.into.websoso.core.resource.R.string.tv_remove_popup_menu_stop_creating
import com.into.websoso.databinding.DialogReportPopupMenuBinding
import com.into.websoso.ui.main.feed.FeedFragment.FeedDialogClickListener

class CreatingFeedDialogFragment :
    BaseDialogFragment<DialogReportPopupMenuBinding>(dialog_report_popup_menu) {
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val onRemoveClick: FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedDialogClickListener
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvReportPopupMenuTitle.text = getString(tv_remove_popup_menu_stop_creating)
        binding.tvReportPopupMenuCancel.text = getString(remove_popup_menu_keep_creating)
        binding.tvReportPopupMenuReport.text = getString(remove_popup_menu_stop_creating)
        binding.tvReportPopupMenuCancel.setOnClickListener { dismiss() }
        binding.tvReportPopupMenuReport.setOnClickListener {
            singleEventHandler.throttleFirst {
                dismiss()
                onRemoveClick()
            }
        }
        dialog?.setCanceledOnTouchOutside(false)
    }

    companion object {
        private const val EVENT = "EVENT"
        const val TAG = "FeedRemoveDialogFragment"

        fun newInstance(event: FeedDialogClickListener): CreatingFeedDialogFragment =
            CreatingFeedDialogFragment().also {
                it.arguments = Bundle().apply {
                    putSerializable(EVENT, event)
                }
            }
    }
}
