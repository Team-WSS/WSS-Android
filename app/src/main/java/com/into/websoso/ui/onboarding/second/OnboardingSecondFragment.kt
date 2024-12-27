package com.into.websoso.ui.onboarding.second

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.into.websoso.R
import com.into.websoso.common.ui.base.BaseFragment
import com.into.websoso.databinding.FragmentOnboardingSecondBinding
import com.into.websoso.ui.onboarding.OnboardingBirthYearBottomSheetDialog
import com.into.websoso.ui.onboarding.OnboardingViewModel
import com.into.websoso.ui.onboarding.model.UserModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingSecondFragment :
    BaseFragment<FragmentOnboardingSecondBinding>(R.layout.fragment_onboarding_second) {

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = onboardingViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupObserver()
        onBirthYearSelectionButtonClick()
    }

    private fun setupObserver() {
        onboardingViewModel.userProfile.observe(viewLifecycleOwner) { userInfo ->
            updateBirthYearTextUi(userInfo)
        }
    }

    private fun updateBirthYearTextUi(userModelInfo: UserModel) {
        val birthYearText =
            if (userModelInfo.birthYear != 0) userModelInfo.birthYear.toString() else getString(R.string.onboarding_second_input_birth_year)
        with(binding.tvOnboardingSecondBirthYearHint) {
            text = birthYearText
            setTextColor(
                ContextCompat.getColor(
                    context,
                    when (userModelInfo.birthYear) {
                        0 -> R.color.gray_200_AEADB3
                        else -> R.color.black
                    }
                )
            )
        }
    }

    private fun onBirthYearSelectionButtonClick() {
        binding.clOnBoardingSecondBirthYear.setOnClickListener {
            showBirthYearBottomSheetDialog()
        }
    }

    private fun showBirthYearBottomSheetDialog() {
        val existingDialog =
            parentFragmentManager.findFragmentByTag(BIRTH_YEAR_BOTTOM_SHEET_DIALOG_TAG)
        if (existingDialog == null) {
            OnboardingBirthYearBottomSheetDialog().show(
                parentFragmentManager,
                BIRTH_YEAR_BOTTOM_SHEET_DIALOG_TAG
            )
        }
    }

    companion object {
        private const val BIRTH_YEAR_BOTTOM_SHEET_DIALOG_TAG = "BirthYearBottomSheetDialog"
    }
}
