package com.teamwss.websoso.ui.novelDetail

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.databinding.DialogNovelAlertBinding

class NovelAlertDialogFragment : BaseDialogFragment<DialogNovelAlertBinding>(R.layout.dialog_novel_alert) {

    private var onAcceptClick: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onAcceptButtonClick()
        onCancelButtonClick()
        setAlertTitle()
        setAlertMessage()
        setAcceptButtonText()
        setCancelButtonText()
    }

    private fun onAcceptButtonClick() {
        binding.onAcceptClick = {
            onAcceptClick?.invoke()
            dismiss()
        }
    }

    private fun onCancelButtonClick() {
        binding.onCancelClick = { dismiss() }
    }

    private fun setAlertTitle() {
        binding.tvNovelAlertTitle.text = arguments?.getString(ALERT_TITLE)
    }

    private fun setAlertMessage() {
        if (binding.tvNovelAlertMessage.text.isEmpty()) {
            binding.tvNovelAlertMessage.visibility = View.GONE
        } else {
            binding.tvNovelAlertMessage.visibility = View.VISIBLE
        }
    }

    private fun setAcceptButtonText() {
        binding.tvNovelAlertAccept.text = arguments?.getString(ACCEPT_BUTTON_TEXT)
    }

    private fun setCancelButtonText() {
        binding.tvNovelAlertCancel.text = arguments?.getString(CANCEL_BUTTON_TEXT)
    }

    companion object {
        const val TAG = "NOVEL_ALERT_DIALOG_FRAGMENT"

        private const val ALERT_MESSAGE = "ALERT_MESSAGE"
        private const val ALERT_TITLE = "ALERT_TITLE"
        private const val ACCEPT_BUTTON_TEXT = "ACCEPT_BUTTON_TEXT"
        private const val CANCEL_BUTTON_TEXT = "CANCEL_BUTTON_TEXT"

        fun newInstance(
            alertTitle: String,
            alertMessage: String = "",
            acceptButtonText: String,
            cancelButtonText: String,
            onAcceptClick: () -> Unit,
        ): NovelAlertDialogFragment = NovelAlertDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ALERT_TITLE, alertTitle)
                putString(ALERT_MESSAGE, alertMessage)
                putString(ACCEPT_BUTTON_TEXT, acceptButtonText)
                putString(CANCEL_BUTTON_TEXT, cancelButtonText)
            }
            this.onAcceptClick = onAcceptClick
        }
    }
}