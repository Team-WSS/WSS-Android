package com.into.websoso.ui.accountInfo

import android.app.AlertDialog
import android.app.AlertDialog.BUTTON_NEGATIVE
import android.app.AlertDialog.BUTTON_POSITIVE
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.getColor
import com.into.websoso.R.color.black
import com.into.websoso.R.layout.activity_account_info
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.model.ResultFrom.ChangeUserInfo
import com.into.websoso.core.common.util.showWebsosoSnackBar
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.resource.R.drawable.ic_novel_detail_check
import com.into.websoso.core.resource.R.string.change_user_info_message
import com.into.websoso.databinding.ActivityAccountInfoBinding
import com.into.websoso.ui.blockedUsers.BlockedUsersActivity
import com.into.websoso.ui.changeUserInfo.ChangeUserInfoActivity
import com.into.websoso.ui.withdraw.first.WithdrawFirstActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountInfoActivity : BaseActivity<ActivityAccountInfoBinding>(activity_account_info) {
    @Inject
    lateinit var tracker: Tracker

    private val accountInfoViewModel: AccountInfoViewModel by viewModels()
    private val startActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        when (result.resultCode) {
            ChangeUserInfo.RESULT_OK -> showChangeUserInfoSuccessMessage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        onLogoutButtonClick()
        onBackButtonClick()
        onWithDrawButtonClick()
        onChangeUserInfoButtonClick()
        onBlockUsersButtonClick()
        onDeleteCacheButtonClick()
    }

    private fun showChangeUserInfoSuccessMessage() {
        showWebsosoSnackBar(
            view = binding.root,
            message = getString(change_user_info_message),
            icon = ic_novel_detail_check,
        )
    }

    private fun bindViewModel() {
        binding.accountInfoViewModel = accountInfoViewModel
        binding.lifecycleOwner = this
    }

    private fun onLogoutButtonClick() {
        binding.clAccountInfoLogout.setOnClickListener {
            LogoutDialogFragment
                .newInstance()
                .show(supportFragmentManager, LogoutDialogFragment.TAG)
        }
    }

    private fun onWithDrawButtonClick() {
        binding.clAccountInfoWithdraw.setOnClickListener {
            tracker.trackEvent("withdraw")
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
            tracker.trackEvent("logout")
            val intent = ChangeUserInfoActivity.getIntent(this)
            startActivityLauncher.launch(intent)
        }
    }

    private fun onBlockUsersButtonClick() {
        binding.clAccountInfoBlockedUserList.setOnClickListener {
            val intent = BlockedUsersActivity.getIntent(this)
            startActivity(intent)
        }
    }

    private fun onDeleteCacheButtonClick() {
        binding.clAccountInfoDeleteCache.setOnClickListener {
            AlertDialog
                .Builder(this)
                .setTitle("캐시 삭제")
                .setMessage("정말 캐시를 삭제하시겠습니까?")
                .setPositiveButton("확인") { _, _ ->
                    accountInfoViewModel.clearCache()
                }.setNegativeButton("취소", null)
                .create()
                .apply {
                    show()
                    getButton(BUTTON_POSITIVE)
                        .setTextColor(getColor(this@AccountInfoActivity, black))
                    getButton(BUTTON_NEGATIVE)
                        .setTextColor(getColor(this@AccountInfoActivity, black))
                }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, AccountInfoActivity::class.java)
    }
}
