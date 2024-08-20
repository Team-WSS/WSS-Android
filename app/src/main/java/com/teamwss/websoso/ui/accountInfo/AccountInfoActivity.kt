package com.teamwss.websoso.ui.accountInfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityAccountInfoBinding
import com.teamwss.websoso.ui.blockedUsers.BlockedUsersActivity
import com.teamwss.websoso.ui.changeUserInfo.ChangeUserInfoActivity
import com.teamwss.websoso.common.ui.base.BindingActivity
import com.teamwss.websoso.ui.withdraw.first.WithdrawFirstActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountInfoActivity :
    BindingActivity<ActivityAccountInfoBinding>(R.layout.activity_account_info) {
    private val accountInfoViewModel: AccountInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        onLogoutButtonClick()
        onBackButtonClick()
        onWithDrawButtonClick()
        onChangeUserInfoButtonClick()
        onBlockUsersButtonClick()
    }

    private fun bindViewModel() {
        binding.accountInfoViewModel = accountInfoViewModel
        binding.lifecycleOwner = this
    }

    private fun onLogoutButtonClick() {
        binding.clAccountInfoLogout.setOnClickListener {
            LogoutDialogFragment.newInstance()
                .show(supportFragmentManager, LogoutDialogFragment.TAG)
        }
    }

    private fun onWithDrawButtonClick() {
        binding.clAccountInfoWithdraw.setOnClickListener {
            val intent = WithdrawFirstActivity.getIntent(this)
            startActivity(intent)
        }
    }

    private fun onBackButtonClick() {
        binding.ivAccountInfoBackButton.setOnClickListener {
            finish()
        }
    }

    private fun onChangeUserInfoButtonClick() {
        binding.clAccountInfoChangeUserInfo.setOnClickListener {
            val intent = ChangeUserInfoActivity.getIntent(this)
            startActivity(intent)
        }
    }

    private fun onBlockUsersButtonClick() {
        binding.clAccountInfoBlockedUserList.setOnClickListener {
            val intent = BlockedUsersActivity.getIntent(this)
            startActivity(intent)
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, AccountInfoActivity::class.java)
        }
    }
}