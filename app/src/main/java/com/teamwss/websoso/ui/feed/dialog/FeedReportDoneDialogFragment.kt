package com.teamwss.websoso.ui.feed.dialog

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogReportDonePopupMenuBinding
import com.teamwss.websoso.ui.common.base.BindingDialogFragment
import com.teamwss.websoso.ui.feed.FeedFragment

class FeedReportDoneDialogFragment :
    BindingDialogFragment<DialogReportDonePopupMenuBinding>(R.layout.dialog_report_done_popup_menu) {
    private val onCheckClick: FeedFragment.FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedFragment.FeedDialogClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.onCheckClick = { onCheckClick() }
        dialog?.setCanceledOnTouchOutside(false)
    }

    companion object {
        private const val EVENT = "EVENT"
        const val TAG = "FeedReportDoneDialogFragment"

        fun newInstance(event: FeedFragment.FeedDialogClickListener): FeedReportDoneDialogFragment =
            FeedReportDoneDialogFragment().also {
                it.arguments = Bundle().apply {
                    putSerializable(EVENT, event)
                }
            }
    }
}
