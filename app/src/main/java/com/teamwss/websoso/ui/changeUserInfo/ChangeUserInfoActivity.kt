package com.teamwss.websoso.ui.changeUserInfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityChangeUserInfoBinding
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.ui.onboarding.OnboardingBirthYearBottomSheetDialog

class ChangeUserInfoActivity :
    BaseActivity<ActivityChangeUserInfoBinding>(R.layout.activity_change_user_info) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onChangeBirthYearClickButton()
        onBackButtonClick()
    }

    private fun onChangeBirthYearClickButton() {
        binding.clChangeUserInfoBirthYear.setOnClickListener {
            showBirthYearBottomSheetDialog()
        }
    }

    private fun showBirthYearBottomSheetDialog() {
        val existingDialog =
            supportFragmentManager.findFragmentByTag(BIRTH_YEAR_BOTTOM_SHEET_DIALOG_TAG)
        if (existingDialog == null) {
            OnboardingBirthYearBottomSheetDialog().show(
                supportFragmentManager,
                BIRTH_YEAR_BOTTOM_SHEET_DIALOG_TAG,
            )
        }
    }

    private fun onBackButtonClick() {
        binding.ivChangeUserInfoBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        private const val BIRTH_YEAR_BOTTOM_SHEET_DIALOG_TAG = "BirthYearBottomSheetDialog"

        fun getIntent(context: Context): Intent {
            return Intent(context, ChangeUserInfoActivity::class.java)
        }
    }
}