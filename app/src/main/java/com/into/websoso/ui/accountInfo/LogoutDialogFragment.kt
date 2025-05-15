package com.into.websoso.ui.accountInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.into.websoso.R
import com.into.websoso.core.auth.AuthClient
import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.core.common.ui.base.BaseDialogFragment
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.collectWithLifecycle
import com.into.websoso.core.common.util.showWebsosoToast
import com.into.websoso.databinding.DialogLogoutBinding
import javax.inject.Inject

class LogoutDialogFragment : BaseDialogFragment<DialogLogoutBinding>(R.layout.dialog_logout) {
    private val accountInfoViewModel: AccountInfoViewModel by activityViewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    @Inject
    lateinit var authClient: Map<AuthPlatform, @JvmSuppressWildcards AuthClient>

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
                UiEffect.ShowToast -> showWebsosoToast(
                    requireContext(),
                    getString(com.into.websoso.core.resource.R.string.novel_rating_save_error),
                    com.into.websoso.core.resource.R.drawable.ic_novel_rating_alert,
                )
            }
        }
    }

    private fun setClickListener() {
        binding.tvLogoutCancelButton.setOnClickListener {
            dismiss()
        }

        binding.tvLogoutButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                accountInfoViewModel.signOut { platform ->
                    authClient[platform]?.signOut()
                }
            }
        }
    }

    companion object {
        const val TAG = "LogoutDialogFragment"

        fun newInstance(): LogoutDialogFragment = LogoutDialogFragment()
    }
}
