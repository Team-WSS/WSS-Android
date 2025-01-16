package com.into.websoso.ui.accountInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseDialogFragment
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.databinding.DialogLogoutBinding
import com.into.websoso.ui.login.LoginActivity

class LogoutDialogFragment : BaseDialogFragment<DialogLogoutBinding>(R.layout.dialog_logout) {
    private val accountInfoViewModel: AccountInfoViewModel by activityViewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        onCancelButtonClick()
        onLogoutButtonClick()
        setupObserver()
    }

    private fun setupObserver() {
        accountInfoViewModel.isLogoutSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                startActivity(LoginActivity.getIntent(requireContext()))
            }
        }
    }

    private fun onCancelButtonClick() {
        binding.tvLogoutCancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun onLogoutButtonClick() {
        binding.tvLogoutButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                accountInfoViewModel.logout()
            }
        }
    }

    companion object {
        const val TAG = "LogoutDialogFragment"

        fun newInstance(): LogoutDialogFragment = LogoutDialogFragment()
    }
}
