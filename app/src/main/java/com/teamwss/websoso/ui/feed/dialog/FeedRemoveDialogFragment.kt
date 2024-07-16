package com.teamwss.websoso.ui.feed.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogRemovePopupMenuBinding
import com.teamwss.websoso.ui.common.base.BindingDialogFragment
import com.teamwss.websoso.ui.feed.FeedFragment.FeedDialogClickListener

class FeedRemoveDialogFragment :
    BindingDialogFragment<DialogRemovePopupMenuBinding>(R.layout.dialog_remove_popup_menu) {
    private val onRemoveClick: FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedDialogClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.onClick = {
            onRemoveClick()
            dismiss()
        }
        initDialog()
    }

    private fun initDialog() {
        dialog?.let {
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setCanceledOnTouchOutside(false)
        }
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
