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

        setupTranslucentOnStatusBar()
        bindViewModel()
        setupObserver()
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
    }

    private fun bindViewModel() {
        binding.withdrawSecondViewModel = withdrawSecondViewModel
        binding.onClick = onWithdrawClick()
        binding.lifecycleOwner = this
    }

    private fun onWithdrawClick() = object : WithdrawClickListener {

        override fun onBackButtonClick() {
            finish()
        }

        override fun onWithdrawReasonRarelyUsingButtonClick() {

        }

        override fun onWithdrawReasonInconvenientButtonClick() {

        }

        override fun onWithdrawReasonWantToDeleteContent() {

        }

        override fun onWithdrawReasonNotExistAnyWantedNovel() {

        }

        override fun onWithdrawReasonEtc() {
            binding.etWithdrawEtc.requestFocus()
        }

        override fun onWithdrawCheckAgreeButtonClick() {
            withdrawSecondViewModel.updateIsWithdrawCheckAgree()
        }

        override fun onWithdrawButtonClick() {
            //TODO 회원탈퇴 로직 호출
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

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, WithdrawSecondActivity::class.java)
        }
    }
}