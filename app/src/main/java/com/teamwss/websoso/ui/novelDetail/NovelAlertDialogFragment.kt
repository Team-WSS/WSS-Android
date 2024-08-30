package com.teamwss.websoso.ui.novelDetail

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.databinding.DialogNovelAlertBinding

class NovelAlertDialogFragment : BaseDialogFragment<DialogNovelAlertBinding>(R.layout.dialog_novel_alert) {

    private var onAcceptClick: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onAcceptButtonClick()
        onCancelButtonClick()
        setupView()
        updateAlertMessageVisibility()
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

    private fun setupView() {
        with(binding) {
            tvNovelAlertTitle.text = arguments?.getString(ALERT_TITLE)
            tvNovelAlertAccept.text = arguments?.getString(ACCEPT_BUTTON_TEXT)
            tvNovelAlertCancel.text = arguments?.getString(CANCEL_BUTTON_TEXT)
        }
    }

    private fun updateAlertMessageVisibility() {
        with(binding.tvNovelAlertMessage) {
            visibility = when (binding.tvNovelAlertMessage.text.isEmpty()) {
                true -> GONE
                false -> VISIBLE
            }
        }
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