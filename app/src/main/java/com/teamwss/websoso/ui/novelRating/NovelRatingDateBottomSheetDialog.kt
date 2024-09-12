package com.teamwss.websoso.ui.novelRating

import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseBottomSheetDialog
import com.teamwss.websoso.databinding.DialogNovelRatingDateBinding
import com.teamwss.websoso.ui.novelDetail.NovelAlertDialogFragment
import com.teamwss.websoso.ui.novelDetail.model.NovelAlertModel
import com.teamwss.websoso.ui.novelRating.model.RatingDateModel

class NovelRatingDateBottomSheetDialog :
    BaseBottomSheetDialog<DialogNovelRatingDateBinding>(R.layout.dialog_novel_rating_date) {
    private val novelRatingViewModel: NovelRatingViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.onClick = onNovelRatingButtonClick()
        bindViewModel()
        setupObserver()
        setupDialogBehavior()
        setupValueChangeListener()
        initNullDate()
    }

    private fun bindViewModel() {
        binding.viewModel = novelRatingViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupObserver() {
        novelRatingViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            binding.npRatingDateDay.formatNumberPicker(
                uiState.maxDayValue,
                "%02d",
            )
            initNumberPickerRange(uiState?.maxDayValue ?: MAX_DAY_VALUE)
            binding.npRatingDateYear.value =
                uiState.novelRatingModel.ratingDateModel.currentStartDate?.first ?: 1
            binding.npRatingDateMonth.value =
                uiState.novelRatingModel.ratingDateModel.currentStartDate?.second ?: 1
            binding.npRatingDateDay.value =
                uiState.novelRatingModel.ratingDateModel.currentStartDate?.third ?: 1
            initNumberPickerValue(uiState.novelRatingModel.ratingDateModel, uiState.isEditingStartDate)
        }
    }

    private fun onNovelRatingButtonClick() =
        object : NovelRatingClickListener {
            override fun onDateEditClick() {}

            override fun onKeywordEditClick() {}

            override fun onNavigateBackClick() {}

            override fun onSaveClick() {
                novelRatingViewModel.updatePreviousDate()
                dismiss()
            }

            override fun onCancelClick() {
                novelRatingViewModel.cancelDateEdit()
                dismiss()
            }

            override fun onClearClick() {
                novelRatingViewModel.cancelDateEdit()
                showClearDateInfoAlertDialog()
                dismiss()
            }

            override fun onReportKeywordClick() {}
        }

    private fun showClearDateInfoAlertDialog() {
        val novelAlertModel = NovelAlertModel(
            title = getString(R.string.novel_rating_date_remove_alert_title),
            acceptButtonText = getString(R.string.novel_rating_date_remove_alert_accept),
            cancelButtonText = getString(R.string.novel_rating_date_remove_alert_cancel),
            onAcceptClick = { novelRatingViewModel.clearCurrentDate() },
        )

        NovelAlertDialogFragment
            .newInstance(novelAlertModel)
            .show(parentFragmentManager, NovelAlertDialogFragment.TAG)

    }

    private fun setupDialogBehavior() {
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.skipCollapsed = true
    }

    private fun initNullDate() {
        novelRatingViewModel.updateNotNullDate()
    }

    private fun initNumberPickerRange(maxDayValue: Int) {
        with(binding) {
            npRatingDateYear.formatNumberPicker(MAX_YEAR_VALUE, "%04d")
            npRatingDateMonth.formatNumberPicker(MAX_MONTH_VALUE, "%02d")
            npRatingDateDay.formatNumberPicker(
                maxDayValue,
                "%02d",
            )
        }
    }

    private fun initNumberPickerValue(ratingDateModel: RatingDateModel, isEditingStartDate: Boolean) {
        with(binding) {
            when (isEditingStartDate) {
                true -> {
                    npRatingDateYear.value = ratingDateModel.currentStartDate?.first ?: 1
                    npRatingDateMonth.value = ratingDateModel.currentStartDate?.second ?: 1
                    npRatingDateDay.value = ratingDateModel.currentStartDate?.third ?: 1
                }

                false -> {
                    npRatingDateYear.value = ratingDateModel.currentEndDate?.first ?: 1
                    npRatingDateMonth.value = ratingDateModel.currentEndDate?.second ?: 1
                    npRatingDateDay.value = ratingDateModel.currentEndDate?.third ?: 1
                }
            }
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
            novelRatingViewModel.updateCurrentDate(
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
        novelRatingViewModel.cancelDateEdit()
        super.onDestroyView()
    }

    companion object {
        const val TAG = "NOVEL_RATING_DATE_BOTTOM_SHEET_DIALOG"
        private const val MAX_YEAR_VALUE = 9999
        private const val MAX_MONTH_VALUE = 12
        private const val MAX_DAY_VALUE = 31
    }
}
