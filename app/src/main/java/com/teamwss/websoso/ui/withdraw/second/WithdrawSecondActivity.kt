package com.teamwss.websoso.ui.withdraw.second

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
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
        onWithdrawEtcEditTextFocusListener()
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
            withdrawSecondViewModel.updateWithdrawReason(getString(R.string.withdraw_reason_rarely_using))
        }

        override fun onWithdrawReasonInconvenientButtonClick() {
            withdrawSecondViewModel.updateWithdrawReason(getString(R.string.withdraw_reason_inconvenient))
        }

        override fun onWithdrawReasonWantToDeleteContentButtonClick() {
            withdrawSecondViewModel.updateWithdrawReason(getString(R.string.withdraw_reason_want_to_delete_content))
        }

        override fun onWithdrawReasonNotExistAnyWantedNovelButtonClick() {
            withdrawSecondViewModel.updateWithdrawReason(getString(R.string.withdraw_reason_not_exist_any_wanted_novel))
        }

        override fun onWithdrawReasonEtcButtonClick() {
            binding.etWithdrawEtc.requestFocus()
            withdrawSecondViewModel.updateWithdrawReason(getString(R.string.withdraw_reason_etc))
        }

        override fun onWithdrawCheckAgreeButtonClick() {
            withdrawSecondViewModel.updateIsWithdrawCheckAgree()
        }

        override fun onWithdrawButtonClick() {
            withdrawSecondViewModel.saveWithdrawReason()
        }
    }

    private fun onWithdrawEtcEditTextFocusListener() {
        binding.etWithdrawEtc.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                withdrawSecondViewModel.updateWithdrawReason(getString(R.string.withdraw_reason_etc))
            }
        }
    }

    private fun setupObserver() {
        withdrawSecondViewModel.isWithdrawCheckAgree.observe(this) { isAgree ->
            updateWithdrawCheckAgreeButtonImage(isAgree)
        }

        withdrawSecondViewModel.withdrawReason.observe(this) { reason ->
            updateWithdrawReasonCheckButtonImage(reason)
        }

        withdrawSecondViewModel.etcReason.observe(this) {
            withdrawSecondViewModel.updateWithdrawButtonEnabled()
        }
    }

    private fun updateWithdrawCheckAgreeButtonImage(isWithdrawAgree: Boolean) {
        val buttonImage = when (isWithdrawAgree) {
            true -> R.drawable.img_account_info_check_selected
            false -> R.drawable.img_account_info_check_unselected
        }
        binding.ivWithdrawCheckAgree.setImageResource(buttonImage)
    }

    private fun updateWithdrawReasonCheckButtonImage(selectedReason: String) {
        updateCheckImageState(
            binding.ivWithdrawReasonRarelyUsing,
            selectedReason,
            getString(R.string.withdraw_reason_rarely_using),
        )
        updateCheckImageState(
            binding.ivWithdrawReasonInconvenient,
            selectedReason,
            getString(R.string.withdraw_reason_inconvenient),
        )
        updateCheckImageState(
            binding.ivWithdrawReasonWantToDeleteContent,
            selectedReason,
            getString(R.string.withdraw_reason_want_to_delete_content),
        )
        updateCheckImageState(
            binding.ivWithdrawReasonNotExistAnyWantedNovel,
            selectedReason,
            getString(R.string.withdraw_reason_not_exist_any_wanted_novel),
        )

        val isEtcSelected: Boolean = selectedReason == getString(R.string.withdraw_reason_etc)
        updateEtcCheckImageState(
            binding.ivWithdrawReasonEtc,
            isEtcSelected,
        )
    }

    private fun updateCheckImageState(
        checkImage: ImageView,
        selectedReason: String,
        reason: String,
    ) {
        when (selectedReason) {
            reason -> checkImage.setImageResource(R.drawable.img_account_info_check_selected)
            else -> checkImage.setImageResource(R.drawable.img_account_info_check_unselected)
        }
    }

    private fun updateEtcCheckImageState(
        checkImage: ImageView,
        isEtcSelected: Boolean
    ) {
        val imageResource = if (isEtcSelected) {
            R.drawable.img_account_info_check_selected
        } else {
            R.drawable.img_account_info_check_unselected
        }
        checkImage.setImageResource(imageResource)
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, WithdrawSecondActivity::class.java)
        }
    }
}