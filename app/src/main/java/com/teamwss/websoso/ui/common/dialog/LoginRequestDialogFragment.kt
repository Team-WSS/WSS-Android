package com.teamwss.websoso.ui.common.dialog

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.databinding.DialogLoginRequestBinding
import com.teamwss.websoso.ui.login.LoginActivity

class LoginRequestDialogFragment :
    BaseDialogFragment<DialogLoginRequestBinding>(R.layout.dialog_login_request) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)

        onGoToLoginButtonClick()
        onCancelTextClick()
    }

    private fun onGoToLoginButtonClick() {
        binding.btnLoginRequestGoToLogin.setOnClickListener {
            val intent = LoginActivity.getIntent(requireContext()).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun onCancelTextClick() {
        binding.tvLoginRequestDismiss.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val TAG = "LoginRequestDialogFragment"

        fun newInstance(): LoginRequestDialogFragment = LoginRequestDialogFragment()
    }
}
