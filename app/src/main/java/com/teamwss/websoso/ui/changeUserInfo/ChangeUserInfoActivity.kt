package com.teamwss.websoso.ui.changeUserInfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityChangeUserInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeUserInfoActivity :
    BaseActivity<ActivityChangeUserInfoBinding>(R.layout.activity_change_user_info) {
    private val changeUserInfoViewModel: ChangeUserInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        onChangeBirthYearButtonClick()
        onBackButtonClick()
        onCompleteButtonClick()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.changeUserInfoViewModel = changeUserInfoViewModel
        binding.lifecycleOwner = this
    }

    private fun onChangeBirthYearButtonClick() {
        binding.clChangeUserInfoBirthYear.setOnClickListener {
            showBirthYearBottomSheetDialog()
        }
    }

    private fun showBirthYearBottomSheetDialog() {
        val existingDialog =
            supportFragmentManager.findFragmentByTag(BIRTH_YEAR_BOTTOM_SHEET_DIALOG_TAG)
        if (existingDialog == null) {
            ChangeBirthYearBottomSheetDialog().show(
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

    private fun onCompleteButtonClick() {
        binding.tvChangeUserInfoCompleteButton.setOnClickListener {
            changeUserInfoViewModel.saveUserInfo()
        }
    }

    private fun setupObserver() {
        changeUserInfoViewModel.isSaveStatusComplete.observe(this) { isComplete ->
            if (isComplete) finish()
        }
    }

    companion object {
        private const val BIRTH_YEAR_BOTTOM_SHEET_DIALOG_TAG = "BirthYearBottomSheetDialog"

        fun getIntent(context: Context): Intent {
            return Intent(context, ChangeUserInfoActivity::class.java)
        }
    }
}