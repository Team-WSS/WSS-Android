package com.into.websoso.ui.common.dialog

import android.os.Bundle
import android.view.View
import com.into.websoso.R
import com.into.websoso.common.ui.base.BaseDialogFragment
import com.into.websoso.databinding.DialogLoginRequestBinding
import com.into.websoso.ui.login.LoginActivity

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
            val intent = LoginActivity.getIntent(requireContext())
            startActivity(intent)
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
