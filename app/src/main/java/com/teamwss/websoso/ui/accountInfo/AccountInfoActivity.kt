package com.teamwss.websoso.ui.accountInfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityAccountInfoBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountInfoActivity :
    BindingActivity<ActivityAccountInfoBinding>(R.layout.activity_account_info) {
    private val accountInfoViewModel: AccountInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupTranslucentOnStatusBar()
        onBackButtonClick()
    }

    private fun bindViewModel() {
        binding.accountInfoViewModel = accountInfoViewModel
        binding.lifecycleOwner = this
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
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