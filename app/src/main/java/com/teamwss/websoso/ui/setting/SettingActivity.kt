package com.teamwss.websoso.ui.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivitySettingBinding
import com.teamwss.websoso.ui.accountInfo.AccountInfoActivity
import com.teamwss.websoso.ui.profileDisclosure.ProfileDisclosureActivity

class SettingActivity : BaseActivity<ActivitySettingBinding>(R.layout.activity_setting) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindClickListener()
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
            val officialUrl = getString(R.string.websoso_official)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(officialUrl))
            startActivity(intent)
        }

        override fun onInquireAndFeedbackButtonClick() {
            val inquireUrl = getString(R.string.inquire_link)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inquireUrl))
            startActivity(intent)
        }

        override fun onAppRatingButtonClick() {
            // TODO 별점 남기기로 이동
        }

        override fun onTermsOfUseButtonClick() {
            // TODO 서비스 이용약관으로 이동
        }

        override fun onBackButtonClick() {
            finish()
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }
    }
}
