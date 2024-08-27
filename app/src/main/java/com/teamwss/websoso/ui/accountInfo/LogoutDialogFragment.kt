package com.teamwss.websoso.ui.accountInfo

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.databinding.DialogLogoutBinding

class LogoutDialogFragment : BaseDialogFragment<DialogLogoutBinding>(R.layout.dialog_logout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCancelButtonClick()
        onLogoutButtonClick()
    }

    private fun onCancelButtonClick() {
        binding.tvLogoutCancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun onLogoutButtonClick() {
        // TODO 로그아웃 후 로그인 화면으로 이동
    }

    companion object {
        const val TAG = "LogoutDialogFragment"

        fun newInstance(): LogoutDialogFragment = LogoutDialogFragment()
    }
}
