package com.into.websoso.ui.accountInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.into.websoso.R
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.core.common.ui.base.BaseDialogFragment
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.collectWithLifecycle
import com.into.websoso.databinding.DialogLogoutBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogoutDialogFragment : BaseDialogFragment<DialogLogoutBinding>(R.layout.dialog_logout) {
    private val accountInfoViewModel: AccountInfoViewModel by activityViewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    @Inject
    lateinit var websosoNavigator: NavigatorProvider

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setClickListener()
        collectUiEffect()
    }

    private fun collectUiEffect() {
        accountInfoViewModel.uiEffect.collectWithLifecycle(viewLifecycleOwner) { uiEffect ->
            when (uiEffect) {
                UiEffect.NavigateToLogin -> websosoNavigator.navigateToLoginActivity()
            }
        }
    }

    private fun setClickListener() {
        binding.tvLogoutCancelButton.setOnClickListener {
            dismiss()
        }

        binding.tvLogoutButton.setOnClickListener {
            singleEventHandler.throttleFirst(event = accountInfoViewModel::signOut)
        }
    }

    companion object {
        const val TAG = "LogoutDialogFragment"

        fun newInstance(): LogoutDialogFragment = LogoutDialogFragment()
    }
}
