package com.teamwss.websoso.ui.feedDetail.dialog

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R.layout.dialog_feed_removed_popup_menu
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.databinding.DialogFeedRemovedPopupMenuBinding
import com.teamwss.websoso.ui.main.feed.FeedFragment.FeedDialogClickListener

class RemovedFeedDialogFragment :
    BaseDialogFragment<DialogFeedRemovedPopupMenuBinding>(dialog_feed_removed_popup_menu) {
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val onCheckClick: FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedDialogClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRemovedFeedMenuCheck.setOnClickListener {
            singleEventHandler.throttleFirst {
                onCheckClick()
                dismiss()
            }
        }
        dialog?.setCanceledOnTouchOutside(false)
    }

    companion object {
        private const val EVENT = "EVENT"
        const val TAG = "RemovedFeedDialogFragment"

        fun newInstance(event: FeedDialogClickListener): RemovedFeedDialogFragment =
            RemovedFeedDialogFragment().also {
                it.arguments = Bundle().apply {
                    putSerializable(EVENT, event)
                }
            }
    }
}
