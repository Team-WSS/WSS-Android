package com.teamwss.websoso.ui.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.model.ResultFrom.EditProfileDisclosure
import com.teamwss.websoso.common.util.showWebsosoSnackBar
import com.teamwss.websoso.databinding.ActivitySettingBinding
import com.teamwss.websoso.ui.accountInfo.AccountInfoActivity
import com.teamwss.websoso.ui.profileDisclosure.ProfileDisclosureActivity

class SettingActivity : BaseActivity<ActivitySettingBinding>(R.layout.activity_setting) {
    private lateinit var activityResultCallback: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityResultCallback =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    EditProfileDisclosure.RESULT_OK -> {
                        val isProfilePublic =
                            result.data?.getBooleanExtra(
                                ProfileDisclosureActivity.IS_PROFILE_PUBLIC,
                                false,
                            )
                        val message: String = when (isProfilePublic) {
                            true -> getString(R.string.profile_disclosure_message, "전체 공개")
                            false -> getString(R.string.profile_disclosure_message, "비공개")
                            else -> error("")
                        }

                        showWebsosoSnackBar(
                            view = binding.root,
                            message = message,
                            icon = R.drawable.ic_novel_detail_check,
                        )
                    }
                }
            }
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
            activityResultCallback.launch(intent)
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
