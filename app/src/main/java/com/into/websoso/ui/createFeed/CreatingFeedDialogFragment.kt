package com.into.websoso.ui.createFeed

import android.os.Bundle
import android.view.View
import com.into.websoso.R.layout.dialog_editing_cancel_feed
import com.into.websoso.R.layout.dialog_report_popup_menu
import com.into.websoso.core.common.ui.base.BaseDialogFragment
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.databinding.DialogEditingCancelFeedBinding
import com.into.websoso.ui.main.feed.FeedFragment.FeedDialogClickListener

class CreatingFeedDialogFragment : BaseDialogFragment<DialogEditingCancelFeedBinding>(dialog_editing_cancel_feed) {
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val onRemoveClick: FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedDialogClickListener
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvEditingCancelFeedAlertCancelButton.setOnClickListener { dismiss() }
        binding.tvEditingCancelFeedCancelButton.setOnClickListener {
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
