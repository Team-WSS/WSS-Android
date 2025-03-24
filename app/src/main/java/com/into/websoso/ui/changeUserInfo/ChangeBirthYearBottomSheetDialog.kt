package com.into.websoso.ui.changeUserInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.into.websoso.R.layout.dialog_onboarding_birth_year
import com.into.websoso.core.common.ui.base.BaseBottomSheetDialog
import com.into.websoso.databinding.DialogOnboardingBirthYearBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class ChangeBirthYearBottomSheetDialog :
    BaseBottomSheetDialog<DialogOnboardingBirthYearBinding>(dialog_onboarding_birth_year) {
    private val changeUserInfoViewModel: ChangeUserInfoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialogBehavior()
        setupNumberPicker()
        setupListeners()
        setupObserver()
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
        }
    }

    private fun setupListeners() {
        binding.btnOnboardingSecondBottomSheetComplete.setOnClickListener {
            val selectedYear: Int = binding.npOnboardingSecondBottomSheetBirthYear.value
            changeUserInfoViewModel.updateBirthYear(selectedYear)
            dismiss()
        }

        binding.ivOnboardingSecondBottomSheetCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupObserver() {
        changeUserInfoViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            binding.npOnboardingSecondBottomSheetBirthYear.value =
                uiState.birthYear
        }
    }

    companion object {
        private var MAX_BIRTH_YEAR: Int = LocalDate.now().year
        private const val MIN_BIRTH_YEAR: Int = 1900
    }
}
