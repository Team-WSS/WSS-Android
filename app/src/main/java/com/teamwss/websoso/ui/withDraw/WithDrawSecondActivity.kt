package com.teamwss.websoso.ui.withdraw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityWithDrawSecondBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class WithdrawSecondActivity :
    BindingActivity<ActivityWithDrawSecondBinding>(R.layout.activity_with_draw_second) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTranslucentOnStatusBar()
        onBackButtonClick()
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
    }

    private fun onBackButtonClick() {
        binding.ivWithdrawBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, WithdrawSecondActivity::class.java)
        }
    }
}