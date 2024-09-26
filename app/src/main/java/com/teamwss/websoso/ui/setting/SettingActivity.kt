package com.teamwss.websoso.ui.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.teamwss.websoso.R.drawable.ic_novel_detail_check
import com.teamwss.websoso.R.layout
import com.teamwss.websoso.R.string.inquire_link
import com.teamwss.websoso.R.string.profile_disclosure_message
import com.teamwss.websoso.R.string.profile_disclosure_private
import com.teamwss.websoso.R.string.profile_disclosure_public
import com.teamwss.websoso.R.string.websoso_official
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.model.ResultFrom.ChangeProfileDisclosure
import com.teamwss.websoso.common.util.showWebsosoSnackBar
import com.teamwss.websoso.databinding.ActivitySettingBinding
import com.teamwss.websoso.ui.accountInfo.AccountInfoActivity
import com.teamwss.websoso.ui.profileDisclosure.ProfileDisclosureActivity

class SettingActivity : BaseActivity<ActivitySettingBinding>(layout.activity_setting) {
    private lateinit var activityResultCallback: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityResultCallback =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    ChangeProfileDisclosure.RESULT_OK -> {
                        showEditProfileDisclosureSuccessMessage(result)
                    }
                }
            }
        bindClickListener()
    }

    private fun showEditProfileDisclosureSuccessMessage(result: ActivityResult) {
        val isProfilePublic =
            result.data?.getBooleanExtra(
                ProfileDisclosureActivity.IS_PROFILE_PUBLIC,
                false,
            )
        val message: String = when (isProfilePublic ?: true) {
            true -> getString(profile_disclosure_message, getString(profile_disclosure_public))
            false -> getString(profile_disclosure_message, getString(profile_disclosure_private))
        }

        showWebsosoSnackBar(
            view = binding.root,
            message = message,
            icon = ic_novel_detail_check,
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
            activityResultCallback.launch(intent)
        }

        override fun onWebsosoOfficialButtonClick() {
            val officialUrl = getString(websoso_official)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(officialUrl))
            startActivity(intent)
        }

        override fun onInquireAndFeedbackButtonClick() {
            val inquireUrl = getString(inquire_link)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inquireUrl))
            startActivity(intent)
        }

        override fun onPrivacyPolicyButtonClick() {
            // TODO 개인정보 처리방침으로 이동
        }

        override fun onTermsOfUseButtonClick() {
            // TODO 서비스 이용약관으로 이동
        }

        override fun onBackButtonClick() {
            finish()
        }
    }

    companion object {
        const val IS_PROFILE_PUBLIC = "isProfilePublic"

        fun getIntent(context: Context): Intent {
            return Intent(context, SettingActivity::class.java)
        }

        fun getIntent(context: Context, isProfilePublic: Boolean): Intent {
            return Intent(context, SettingActivity::class.java).apply {
                putExtra(IS_PROFILE_PUBLIC, isProfilePublic)
            }
        }
    }
}
