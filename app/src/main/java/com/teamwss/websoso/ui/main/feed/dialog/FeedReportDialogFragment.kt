package com.teamwss.websoso.ui.main.feed.dialog

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.databinding.DialogReportPopupMenuBinding
import com.teamwss.websoso.ui.main.feed.FeedFragment.FeedDialogClickListener

class FeedReportDialogFragment :
    BaseDialogFragment<DialogReportPopupMenuBinding>(R.layout.dialog_report_popup_menu) {
    private val reportTitle: String? by lazy { arguments?.getString(TITLE) }
    private val onReportClick: FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedDialogClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title = reportTitle
        binding.onCancelClick = { dismiss() }
        binding.onReportClick = {
            onReportClick()
            FeedReportDoneDialogFragment.newInstance { dismiss() }
                .show(childFragmentManager, FeedReportDoneDialogFragment.TAG)
        }
        dialog?.setCanceledOnTouchOutside(false)
    }

    companion object {
        private const val EVENT = "EVENT"
        private const val TITLE = "TITLE"
        const val TAG = "FeedReportDialogFragment"

        fun newInstance(
            title: String,
            event: FeedDialogClickListener,
        ): FeedReportDialogFragment = FeedReportDialogFragment().also {
            it.arguments = Bundle().apply {
                putString(TITLE, title)
                putSerializable(EVENT, event)
            }
        }
    }
}
