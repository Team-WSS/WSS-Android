package com.teamwss.websoso.ui.withdraw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityWithdrawFirstBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class WithdrawFirstActivity :
    BindingActivity<ActivityWithdrawFirstBinding>(R.layout.activity_withdraw_first) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTranslucentOnStatusBar()
        onWithDrawCheckButtonClick()
        onBackButtonClick()
    }

    private fun onWithDrawCheckButtonClick() {
        binding.tvWithdrawCheckButton.setOnClickListener {
            val intent = WithdrawSecondActivity.getIntent(this)
            startActivity(intent)
        }
    }

    private fun onBackButtonClick() {
        binding.ivWithdrawBackButton.setOnClickListener {
            finish()
        }
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, WithdrawFirstActivity::class.java)
        }
    }
}