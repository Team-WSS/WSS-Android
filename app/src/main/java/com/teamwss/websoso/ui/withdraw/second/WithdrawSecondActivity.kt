package com.teamwss.websoso.ui.withdraw.second

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityWithdrawSecondBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WithdrawSecondActivity :
    BindingActivity<ActivityWithdrawSecondBinding>(R.layout.activity_withdraw_second) {
    private val withdrawSecondViewModel: WithdrawSecondViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupTranslucentOnStatusBar()
        onBackButtonClick()
        onWithdrawCheckAgreeButtonClick()
        setupObserver()
        onWithdrawButtonClick()
    }

    private fun bindViewModel() {
        binding.withdrawSecondViewModel = withdrawSecondViewModel
        binding.lifecycleOwner = this
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

    private fun onWithdrawCheckAgreeButtonClick() {
        binding.clWithdrawCheckAgreeButton.setOnClickListener {
            withdrawSecondViewModel.updateIsWithdrawCheckAgree()
        }
    }

    private fun setupObserver() {
        withdrawSecondViewModel.isWithdrawCheckAgree.observe(this) { isAgree ->
            updateWithdrawCheckAgreeButtonImage(isAgree)
        }
    }

    private fun updateWithdrawCheckAgreeButtonImage(isWithdrawAgree: Boolean) {
        val buttonImage = when (isWithdrawAgree) {
            true -> R.drawable.img_account_info_check_selected
            false -> R.drawable.img_account_info_check_unselected
        }
        binding.ivWithdrawCheckAgree.setImageResource(buttonImage)
    }

    private fun onWithdrawButtonClick() {
        binding.tvWithdrawButton.setOnClickListener {
            // TODO 회원탈퇴 함수 호출
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, WithdrawSecondActivity::class.java)
        }
    }
}