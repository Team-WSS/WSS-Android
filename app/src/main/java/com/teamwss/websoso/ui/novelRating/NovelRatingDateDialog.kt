package com.teamwss.websoso.ui.novelRating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogNovelRatingDateBinding

class NovelRatingDateDialog : BottomSheetDialogFragment() {
    private var _binding: DialogNovelRatingDateBinding? = null
    private val binding: DialogNovelRatingDateBinding get() = requireNotNull(_binding)
    private val viewModel: NovelRatingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.WebsosoBottomSheetTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogNovelRatingDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupDataBinding()
        setupDialogBehavior()
        initNullDate()
        initNumberPickerRange()
        observeDayRange()
        setupValueChangeListener()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.cancelDateEdit = ::cancelDateEdit
        binding.saveDateEdit = ::saveDateEdit
        binding.clearCurrentDate = ::clearCurrentDate
    }

    private fun setupDialogBehavior() {
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.skipCollapsed = true
    }

    private fun initNullDate() {
        viewModel.createNotNullDate()
    }

    private fun initNumberPickerRange() {
        setupNumberPicker(binding.npRatingDateYear, 1, MAX_YEAR_VALUE, "%04d")
        setupNumberPicker(binding.npRatingDateMonth, 1, MAX_MONTH_VALUE, "%02d")
        setupNumberPicker(binding.npRatingDateDay, 1, viewModel.maxDayValue.value ?: MAX_DAY_VALUE, "%02d")
    }

    private fun observeDayRange() {
        viewModel.maxDayValue.observe(viewLifecycleOwner) { maxDayValue ->
            setupNumberPicker(binding.npRatingDateDay, 1, maxDayValue, "%02d")
        }
    }

    private fun setupNumberPicker(
        numberPicker: NumberPicker,
        minValue: Int,
        maxValue: Int,
        format: String,
    ) {
        with(numberPicker) {
            wrapSelectorWheel = false
            this.minValue = minValue
            this.maxValue = maxValue
            setFormatter { String.format(format, it) }
        }
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

    fun saveDateEdit() {
        viewModel.updatePreviousDate()
        dismiss()
    }

    fun clearCurrentDate() {
        viewModel.clearCurrentDate()
        dismiss()
    }

    fun cancelDateEdit() {
        viewModel.cancelDateEdit()
        dismiss()
    }

    override fun onDestroyView() {
        viewModel.cancelDateEdit()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val MAX_YEAR_VALUE = 9999
        private const val MAX_MONTH_VALUE = 12
        private const val MAX_DAY_VALUE = 31
    }
}
