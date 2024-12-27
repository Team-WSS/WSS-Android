package com.into.websoso.ui.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.into.websoso.R
import com.into.websoso.R.drawable.ic_novel_detail_check
import com.into.websoso.R.string.inquire_link
import com.into.websoso.R.string.privacy_policy_link
import com.into.websoso.R.string.profile_disclosure_message
import com.into.websoso.R.string.profile_disclosure_private
import com.into.websoso.R.string.profile_disclosure_public
import com.into.websoso.R.string.terms_of_use_link
import com.into.websoso.R.string.websoso_official
import com.into.websoso.common.ui.base.BaseActivity
import com.into.websoso.common.ui.model.ResultFrom.ChangeProfileDisclosure
import com.into.websoso.common.util.showWebsosoSnackBar
import com.into.websoso.databinding.ActivitySettingBinding
import com.into.websoso.ui.accountInfo.AccountInfoActivity
import com.into.websoso.ui.profileDisclosure.ProfileDisclosureActivity

class SettingActivity : BaseActivity<ActivitySettingBinding>(R.layout.activity_setting) {
    private val startActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            ChangeProfileDisclosure.RESULT_OK -> showEditProfileDisclosureSuccessMessage(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindClickListener()
    }

    private fun showEditProfileDisclosureSuccessMessage(result: ActivityResult) {
        val isProfilePublic =
            result.data?.getBooleanExtra(
                ProfileDisclosureActivity.IS_PROFILE_PUBLIC,
                true,
            ) ?: true
        val message: String = when (isProfilePublic) {
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
            startActivityLauncher.launch(intent)
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
            val inquireUrl = getString(privacy_policy_link)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inquireUrl))
            startActivity(intent)
        }

        override fun onTermsOfUseButtonClick() {
            val inquireUrl = getString(terms_of_use_link)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inquireUrl))
            startActivity(intent)
        }

        override fun onBackButtonClick() {
            finish()
        }
    }

    companion object {
        private const val IS_PROFILE_PUBLIC = "isProfilePublic"

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
