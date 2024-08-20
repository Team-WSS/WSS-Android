package com.teamwss.websoso.ui.withdraw.second

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityWithdrawSecondBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class WithdrawSecondActivity :
    BindingActivity<ActivityWithdrawSecondBinding>(R.layout.activity_withdraw_second) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackButtonClick()
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