package com.teamwss.websoso.ui.novelRating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.NumberPicker
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogRatingDateBinding

class RatingDateDialog : BottomSheetDialogFragment() {
    private var _binding: DialogRatingDateBinding? = null
    private val binding: DialogRatingDateBinding get() = requireNotNull(_binding)
    private val viewModel: NovelRatingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.WebsosoBottomSheetTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRatingDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDataBinding()
        setupDialogBehavior()
        setupDefaultNumberPickerRange()
        observeDayRange()
        setupValueChangeListener()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.dialog = this
        binding.lifecycleOwner = this
    }

    private fun setupDialogBehavior() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val backgroundView =
            dialog?.window?.decorView?.findViewById<View>(com.google.android.material.R.id.touch_outside)

        bottomSheet.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true

            backgroundView?.setOnClickListener {
                cancelDateEdit()
            }
        }
    }

    private fun setupDefaultNumberPickerRange() {
        setupNumberPicker(binding.npRatingDateYear, 1, 9999, "%04d")
        setupNumberPicker(binding.npRatingDateMonth, 1, 12, "%02d")
        setupNumberPicker(binding.npRatingDateDay, 1, viewModel.maxDayValue.value ?: 31, "%02d")
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
        format: String
    ) {
        with(numberPicker) {
            wrapSelectorWheel = false
            this.minValue = minValue
            this.maxValue = maxValue
            setFormatter { String.format(format, it) }
        }
    }

    private fun setupValueChangeListener() {
        binding.npRatingDateYear.setOnValueChangedListener { _, _, _ ->
            viewModel.updateCurrentDate(
                Triple(
                    binding.npRatingDateYear.value,
                    binding.npRatingDateMonth.value,
                    binding.npRatingDateDay.value
                )
            )
        }
        binding.npRatingDateMonth.setOnValueChangedListener { _, _, _ ->
            viewModel.updateCurrentDate(
                Triple(
                    binding.npRatingDateYear.value,
                    binding.npRatingDateMonth.value,
                    binding.npRatingDateDay.value
                )
            )
        }
        binding.npRatingDateDay.setOnValueChangedListener { _, _, _ ->
            viewModel.updateCurrentDate(
                Triple(
                    binding.npRatingDateYear.value,
                    binding.npRatingDateMonth.value,
                    binding.npRatingDateDay.value
                )
            )
        }
    }

    fun saveDateEdit() {
        viewModel.updatePastDate()
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
        _binding = null
        super.onDestroyView()
    }
}
