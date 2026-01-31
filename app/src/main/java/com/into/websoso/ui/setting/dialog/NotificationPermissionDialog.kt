package com.into.websoso.ui.setting.dialog

import android.os.Bundle
import android.view.View
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseDialogFragment
import com.into.websoso.databinding.DialogNotificationPermissionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationPermissionDialog : BaseDialogFragment<DialogNotificationPermissionBinding>(R.layout.dialog_notification_permission) {
    private var onSetUpClickListener: (() -> Unit)? = null

    fun setOnSetUpClickListener(block: () -> Unit) {
        onSetUpClickListener = block
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        onNextTimeButtonClick()
        onSetUpButtonClick()
    }

    private fun onNextTimeButtonClick() {
        binding.tvNotificationPermissionNextTime.setOnClickListener {
            dismiss()
        }
    }

    private fun onSetUpButtonClick() {
        binding.tvNotificationPermissionToSetUp.setOnClickListener {
            onSetUpClickListener?.invoke()
            dismiss()
        }
    }

    companion object {
        const val NOTIFICATION_PERMISSION_DIALOG_TAG = "NotificationPermissionDialog"

        fun newInstance(): NotificationPermissionDialog = NotificationPermissionDialog()
    }
}
