package com.into.websoso.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.into.websoso.R
import com.into.websoso.common.ui.base.BaseBottomSheetDialog
import com.into.websoso.databinding.DialogOnboardingBirthYearBinding
import java.time.LocalDate

class OnboardingBirthYearBottomSheetDialog :
    BaseBottomSheetDialog<DialogOnboardingBirthYearBinding>(
        R.layout.dialog_onboarding_birth_year
    ) {
    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupDialogBehavior()
        setupNumberPicker()
        setupListeners()
    }

    private fun setupDialogBehavior() {
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.skipCollapsed = true
    }

    private fun setupNumberPicker() {
        val numberPicker = binding.npOnboardingSecondBottomSheetBirthYear
        numberPicker.apply {
            minValue = MIN_BIRTH_YEAR
            maxValue = MAX_BIRTH_YEAR
            value = INIT_BIRTH_YEAR
        }
    }

    private fun setupListeners() {
        binding.btnOnboardingSecondBottomSheetComplete.setOnClickListener {
            val selectedYear = binding.npOnboardingSecondBottomSheetBirthYear.value
            onboardingViewModel.updateUserBirthYear(selectedYear)
            dismiss()
        }

        binding.ivOnboardingSecondBottomSheetCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private var MAX_BIRTH_YEAR: Int = LocalDate.now().year
        private const val MIN_BIRTH_YEAR: Int = 1900
        private const val INIT_BIRTH_YEAR: Int = 2000
    }
}
