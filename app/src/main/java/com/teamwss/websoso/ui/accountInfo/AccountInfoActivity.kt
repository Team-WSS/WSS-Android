package com.teamwss.websoso.ui.accountInfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityAccountInfoBinding
import com.teamwss.websoso.ui.blockUsers.BlockUsersActivity
import com.teamwss.websoso.ui.changeUserInfo.ChangeGenderAndAgeActivity
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.withDraw.WithDrawFirstActivity

class AccountInfoActivity :
    BindingActivity<ActivityAccountInfoBinding>(R.layout.activity_account_info) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTranslucentOnStatusBar()
        onLogoutButtonClick()
        onBackButtonClick()
        onWithDrawButtonClick()
        onChangeGenderAndAgeButtonClick()
        onBlockUsersButtonClick()
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

    private fun onWithDrawButtonClick() {
        binding.clAccountInfoWithDraw.setOnClickListener {
            val intent = WithDrawFirstActivity.getIntent(this)
            startActivity(intent)
        }
    }

    private fun onBackButtonClick() {
        binding.ivAccountInfoBackButton.setOnClickListener {
            finish()
        }
    }

    private fun onChangeGenderAndAgeButtonClick() {
        binding.clAccountInfoChangeUserInfo.setOnClickListener {
            val intent = ChangeGenderAndAgeActivity.getIntent(this)
            startActivity(intent)
        }
    }

    private fun onBlockUsersButtonClick() {
        binding.clAccountInfoBlockUserList.setOnClickListener {
            val intent = BlockUsersActivity.getIntent(this)
            startActivity(intent)
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, AccountInfoActivity::class.java)
        }
    }
}