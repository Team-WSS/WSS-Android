package com.teamwss.websoso.ui.feed.dialog

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogReportDonePopupMenuBinding
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.ui.feed.FeedFragment.FeedDialogClickListener

class FeedReportDoneDialogFragment :
    BaseDialogFragment<DialogReportDonePopupMenuBinding>(R.layout.dialog_report_done_popup_menu) {
    private val onCheckClick: FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedDialogClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.onCheckClick = {
            onCheckClick()
            dismiss()
        }
        dialog?.setCanceledOnTouchOutside(false)
    }

    companion object {
        private const val EVENT = "EVENT"
        const val TAG = "FeedReportDoneDialogFragment"

        fun newInstance(event: FeedDialogClickListener): FeedReportDoneDialogFragment =
            FeedReportDoneDialogFragment().also {
                it.arguments = Bundle().apply {
                    putSerializable(EVENT, event)
                }
            }
    }
}
