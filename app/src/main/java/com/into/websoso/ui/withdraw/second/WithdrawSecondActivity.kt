package com.into.websoso.ui.withdraw.second

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import com.into.websoso.R.layout.activity_withdraw_second
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.resource.R.drawable.img_account_info_check_selected
import com.into.websoso.core.resource.R.drawable.img_account_info_check_unselected
import com.into.websoso.core.resource.R.string.withdraw_reason_etc
import com.into.websoso.core.resource.R.string.withdraw_reason_inconvenient
import com.into.websoso.core.resource.R.string.withdraw_reason_not_exist_any_wanted_novel
import com.into.websoso.core.resource.R.string.withdraw_reason_rarely_using
import com.into.websoso.core.resource.R.string.withdraw_reason_want_to_delete_content
import com.into.websoso.databinding.ActivityWithdrawSecondBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WithdrawSecondActivity : BaseActivity<ActivityWithdrawSecondBinding>(activity_withdraw_second) {
    private val withdrawSecondViewModel: WithdrawSecondViewModel by viewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    @Inject
    lateinit var websosoNavigator: NavigatorProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        onWithdrawEtcEditTextFocusListener()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.withdrawSecondViewModel = withdrawSecondViewModel
        binding.onClick = onWithdrawClick()
        binding.lifecycleOwner = this
    }

    private fun onWithdrawClick() =
        object : WithdrawClickListener {
            override fun onBackButtonClick() {
                finish()
            }

            override fun onWithdrawReasonRarelyUsingButtonClick() {
                withdrawSecondViewModel.updateWithdrawReason(getString(withdraw_reason_rarely_using))
            }

            override fun onWithdrawReasonInconvenientButtonClick() {
                withdrawSecondViewModel.updateWithdrawReason(getString(withdraw_reason_inconvenient))
            }

            override fun onWithdrawReasonWantToDeleteContentButtonClick() {
                withdrawSecondViewModel.updateWithdrawReason(
                    getString(
                        withdraw_reason_want_to_delete_content,
                    ),
                )
            }

            override fun onWithdrawReasonNotExistAnyWantedNovelButtonClick() {
                withdrawSecondViewModel.updateWithdrawReason(
                    getString(
                        withdraw_reason_not_exist_any_wanted_novel,
                    ),
                )
            }

            override fun onWithdrawReasonEtcButtonClick() {
                binding.etWithdrawEtc.requestFocus()
                withdrawSecondViewModel.updateWithdrawReason(getString(withdraw_reason_etc))
            }

            override fun onWithdrawCheckAgreeButtonClick() {
                withdrawSecondViewModel.updateIsWithdrawAgreementChecked()
            }

            override fun onWithdrawButtonClick() {
                singleEventHandler.throttleFirst(event = withdrawSecondViewModel::withdraw)
            }
        }

    private fun onWithdrawEtcEditTextFocusListener() {
        binding.etWithdrawEtc.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                withdrawSecondViewModel.updateWithdrawReason(getString(withdraw_reason_etc))
            }
        }
    }

    private fun setupObserver() {
        withdrawSecondViewModel.isWithdrawAgreementChecked.observe(this) { isAgree ->
            updateWithdrawCheckAgreeButtonImage(isAgree)
        }

        withdrawSecondViewModel.withdrawReason.observe(this) { reason ->
            updateWithdrawReasonCheckButtonImage(reason)
        }

        withdrawSecondViewModel.isWithDrawSuccess.observe(this) { isWithdrawSuccess ->
            if (isWithdrawSuccess) websosoNavigator.navigateToLoginActivity()
        }
    }

    private fun updateWithdrawCheckAgreeButtonImage(isWithdrawAgree: Boolean) {
        binding.ivWithdrawCheckAgree.isActivated = isWithdrawAgree
    }

    private fun updateWithdrawReasonCheckButtonImage(selectedReason: String) {
        updateCheckImageState(
            binding.ivWithdrawReasonRarelyUsing,
            selectedReason,
            getString(withdraw_reason_rarely_using),
        )
        updateCheckImageState(
            binding.ivWithdrawReasonInconvenient,
            selectedReason,
            getString(withdraw_reason_inconvenient),
        )
        updateCheckImageState(
            binding.ivWithdrawReasonWantToDeleteContent,
            selectedReason,
            getString(withdraw_reason_want_to_delete_content),
        )
        updateCheckImageState(
            binding.ivWithdrawReasonNotExistAnyWantedNovel,
            selectedReason,
            getString(withdraw_reason_not_exist_any_wanted_novel),
        )

        val isEtcSelected: Boolean = selectedReason == getString(withdraw_reason_etc)
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
            reason -> checkImage.setImageResource(img_account_info_check_selected)
            else -> checkImage.setImageResource(img_account_info_check_unselected)
        }
    }

    private fun updateEtcCheckImageState(
        checkImage: ImageView,
        isEtcSelected: Boolean,
    ) {
        val imageResource = if (isEtcSelected) {
            img_account_info_check_selected
        } else {
            img_account_info_check_unselected
        }
        checkImage.setImageResource(imageResource)
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, WithdrawSecondActivity::class.java)
    }
}
