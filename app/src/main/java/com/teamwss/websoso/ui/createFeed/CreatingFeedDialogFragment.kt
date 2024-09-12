package com.teamwss.websoso.ui.createFeed

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R.layout.dialog_remove_popup_menu
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.databinding.DialogRemovePopupMenuBinding
import com.teamwss.websoso.ui.main.feed.FeedFragment.FeedDialogClickListener
import com.teamwss.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment

class CreatingFeedDialogFragment :
    BaseDialogFragment<DialogRemovePopupMenuBinding>(dialog_remove_popup_menu) {
    private val onRemoveClick: FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedDialogClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRemovePopupMenuCancel.setOnClickListener { dismiss() }
        binding.tvRemovePopupMenuRemove.setOnClickListener {
            dismiss()
            onRemoveClick()
        }
        dialog?.setCanceledOnTouchOutside(false)
    }

    companion object {
        private const val EVENT = "EVENT"
        const val TAG = "FeedRemoveDialogFragment"

        fun newInstance(event: FeedDialogClickListener): FeedRemoveDialogFragment =
            FeedRemoveDialogFragment().also {
                it.arguments = Bundle().apply {
                    putSerializable(EVENT, event)
                }
            }
    }
}
