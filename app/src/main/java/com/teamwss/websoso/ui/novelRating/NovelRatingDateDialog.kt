package com.teamwss.websoso.ui.novelRating

import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogNovelRatingDateBinding
import com.teamwss.websoso.ui.common.base.BindingBottomSheetDialog

class NovelRatingDateDialog :
    BindingBottomSheetDialog<DialogNovelRatingDateBinding>(R.layout.dialog_novel_rating_date) {
    private val viewModel: NovelRatingViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.onClick = onNovelRatingButtonClick()
        bindViewModel()
        setupObserver()
        setupDialogBehavior()
        initNullDate()
        initNumberPickerRange()
        setupValueChangeListener()
    }

    private fun bindViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            binding.npRatingDateDay.formatNumberPicker(
                uiState.maxDayValue,
                "%02d",
            )
        }
    }

    private fun onNovelRatingButtonClick() =
        object : NovelRatingClickListener {
            override fun onDateEditClick() {}

            override fun onKeywordEditClick() {}

            override fun onNavigateBackClick() {}

            override fun onSaveClick() {
                viewModel.updatePreviousDate()
                dismiss()
            }

            override fun onCancelClick() {
                viewModel.cancelDateEdit()
                dismiss()
            }

            override fun onClearClick() {
                viewModel.clearCurrentDate()
                dismiss()
            }
        }

    private fun setupDialogBehavior() {
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.skipCollapsed = true
    }

    private fun initNullDate() {
        viewModel.updateNotNullDate()
    }

    private fun initNumberPickerRange() {
        with(binding) {
            npRatingDateYear.formatNumberPicker(MAX_YEAR_VALUE, "%04d")
            npRatingDateMonth.formatNumberPicker(MAX_MONTH_VALUE, "%02d")
            npRatingDateDay.formatNumberPicker(
                viewModel?.uiState?.value?.maxDayValue ?: MAX_DAY_VALUE,
                "%02d",
            )
        }
    }

    private fun NumberPicker.formatNumberPicker(
        maxValue: Int,
        format: String,
    ) {
        wrapSelectorWheel = false
        this.minValue = 1
        this.maxValue = maxValue
        setFormatter { String.format(format, it) }
    }

    private fun setupValueChangeListener() {
        val updateCurrentDate = {
            viewModel.updateCurrentDate(
                Triple(
                    binding.npRatingDateYear.value,
                    binding.npRatingDateMonth.value,
                    binding.npRatingDateDay.value,
                ),
            )
        }
        with(binding) {
            npRatingDateYear.setOnValueChangedListener { _, _, _ -> updateCurrentDate() }
            npRatingDateMonth.setOnValueChangedListener { _, _, _ -> updateCurrentDate() }
            npRatingDateDay.setOnValueChangedListener { _, _, _ -> updateCurrentDate() }
        }
    }

    override fun onDestroyView() {
        viewModel.cancelDateEdit()
        super.onDestroyView()
    }

    companion object {
        private const val MAX_YEAR_VALUE = 9999
        private const val MAX_MONTH_VALUE = 12
        private const val MAX_DAY_VALUE = 31
    }
}
