package com.teamwss.websoso.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivitySettingBinding
import com.teamwss.websoso.ui.accountInfo.AccountInfoActivity
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.profileDisclosure.ProfileDisclosureActivity

class SettingActivity : BindingActivity<ActivitySettingBinding>(R.layout.activity_setting) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTranslucentOnStatusBar()
        bindClickListener()
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
    }

    private fun bindClickListener() {
        binding.onClick = onSettingClickListener()
        binding.lifecycleOwner = this
    }

    private fun onSettingClickListener() = object : SettingClickListener {

        override fun onUserAccountInfoButtonClick() {
            val intent = AccountInfoActivity.getIntent(this@SettingActivity)
            startActivity(intent)
        }

        override fun onProfileDisclosureButtonClick() {
            val intent = ProfileDisclosureActivity.getIntent(this@SettingActivity)
            startActivity(intent)
        }

        override fun onWebsosoOfficialButtonClick() {
            // TODO 웹소소 공식 계정으로 연결
        }

        override fun onInquireAndFeedbackButtonClick() {
            // TODO 문의하기 / 피드백으로 연결
        }

        override fun onAppRatingButtonClick() {
            // TODO 별점 남기기로 이동
        }

        override fun onTermsOfUseButtonClick() {
            // TODO 서비스 이용약관으로 이동
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}