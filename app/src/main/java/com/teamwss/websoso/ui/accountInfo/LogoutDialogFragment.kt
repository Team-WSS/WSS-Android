package com.teamwss.websoso.ui.accountInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.databinding.DialogLogoutBinding
import com.teamwss.websoso.ui.login.LoginActivity

class LogoutDialogFragment : BaseDialogFragment<DialogLogoutBinding>(R.layout.dialog_logout) {
    private val accountInfoViewModel: AccountInfoViewModel by activityViewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
