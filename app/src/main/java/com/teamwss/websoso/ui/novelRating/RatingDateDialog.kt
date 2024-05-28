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
        setupDialogBehavior()
        setupDateRange()
        setupCancelClickListener()
    }

    private fun setupDialogBehavior() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        bottomSheet.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    private fun setupDateRange() {
        setupNumberPicker(binding.npRatingDateYear, 1, 9999, "%04d")
        setupNumberPicker(binding.npRatingDateMonth, 1, 12, "%02d")
        setupNumberPicker(binding.npRatingDateDay, 1, 31, "%02d")
    }

    private fun setupNumberPicker(numberPicker: NumberPicker, minValue: Int, maxValue: Int, format: String) {
        with(numberPicker) {
            wrapSelectorWheel = false
            this.minValue = minValue
            this.maxValue = maxValue
            setFormatter { String.format(format, it) }
        }
    }


    private fun setupCancelClickListener() {
        binding.ivRatingDateCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
