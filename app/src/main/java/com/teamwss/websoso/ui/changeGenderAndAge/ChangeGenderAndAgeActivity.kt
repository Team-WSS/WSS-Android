package com.teamwss.websoso.ui.changeGenderAndAge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityChangeGenderAndAgeBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.onboarding.OnboardingBirthYearBottomSheetDialog

class ChangeGenderAndAgeActivity :
    BindingActivity<ActivityChangeGenderAndAgeBinding>(R.layout.activity_change_gender_and_age) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTranslucentOnStatusBar()
        onChangeBirthYearClickButton()
        onBackButtonClick()
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
    }

    private fun onChangeBirthYearClickButton() {
        binding.clChangeGenderAndAgeBirthYear.setOnClickListener {
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
        binding.ivChangeGenderAndAgeBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        private const val BIRTH_YEAR_BOTTOM_SHEET_DIALOG_TAG = "BirthYearBottomSheetDialog"

        fun getIntent(context: Context): Intent {
            return Intent(context, ChangeGenderAndAgeActivity::class.java)
        }
    }
}