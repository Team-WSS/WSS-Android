package com.teamwss.websoso.ui.createFeed

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import com.teamwss.websoso.R.layout.dialog_remove_popup_menu
import com.teamwss.websoso.R.string.tv_remove_popup_menu_stop_creating
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.databinding.DialogRemovePopupMenuBinding
import com.teamwss.websoso.ui.main.feed.FeedFragment.FeedDialogClickListener

class CreatingFeedDialogFragment :
    BaseDialogFragment<DialogRemovePopupMenuBinding>(dialog_remove_popup_menu) {
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val onRemoveClick: FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedDialogClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRemovePopupMenuTitle.visibility = GONE
        binding.tvRemovePopupMenuDescription.text = getString(tv_remove_popup_menu_stop_creating)
        binding.tvRemovePopupMenuCancel.setOnClickListener { dismiss() }
        binding.tvRemovePopupMenuRemove.setOnClickListener {
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
