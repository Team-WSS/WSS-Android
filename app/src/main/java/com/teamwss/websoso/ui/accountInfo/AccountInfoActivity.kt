package com.teamwss.websoso.ui.accountInfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityAccountInfoBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class AccountInfoActivity :
    BindingActivity<ActivityAccountInfoBinding>(R.layout.activity_account_info) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTranslucentOnStatusBar()
        onLogoutButtonClick()
        onBackButtonClick()
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
    }

    private fun onLogoutButtonClick() {
        binding.clAccountInfoLogout.setOnClickListener {
            LogoutDialogFragment.newInstance()
                .show(supportFragmentManager, LogoutDialogFragment.TAG)
        }
    }

    private fun onBackButtonClick() {
        binding.ivAccountInfoBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, AccountInfoActivity::class.java)
        }
    }
}