package com.into.websoso.ui.detailExploreResult

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseBottomSheetDialog
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.databinding.DialogDetailExploreBinding
import com.into.websoso.ui.detailExplore.model.SelectedFragmentTitle
import com.into.websoso.ui.detailExplore.model.SelectedFragmentTitle.INFO
import com.into.websoso.ui.detailExplore.model.SelectedFragmentTitle.KEYWORD

class DetailExploreResultDialogBottomSheet : BaseBottomSheetDialog<DialogDetailExploreBinding>(R.layout.dialog_detail_explore) {
    private val detailExploreResultInfoFragment: DetailExploreResultInfoFragment by lazy { DetailExploreResultInfoFragment() }
    private val detailExploreResultKeywordFragment: DetailExploreResultKeywordFragment by lazy { DetailExploreResultKeywordFragment() }
    private val detailExploreResultViewModel: DetailExploreResultViewModel by activityViewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
        initDetailExploreFragment()
        onReplaceFragmentButtonClick()
        onBottomSheetExitButtonClick()
        setupObserver()
    }

    private fun setupBottomSheet() {
        (dialog as BottomSheetDialog).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            behavior.isHideable = false
            setCancelable(false)
        }
    }

    private fun initDetailExploreFragment() {
        childFragmentManager.commit {
            add(
                R.id.fcv_detail_explore,
                detailExploreResultInfoFragment,
            )
        }
    }

    private fun onReplaceFragmentButtonClick() {
        binding.tvDetailExploreInfoButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                switchFragment(INFO)
            }
        }

        binding.tvDetailExploreKeywordButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                switchFragment(KEYWORD)
            }
        }
    }

    private fun switchFragment(selectedFragmentTitle: SelectedFragmentTitle) {
        val fragmentToShow = when (selectedFragmentTitle) {
            INFO -> detailExploreResultInfoFragment
            KEYWORD -> {
                if (childFragmentManager.findFragmentById(R.id.fcv_detail_explore) !is DetailExploreResultKeywordFragment) {
                    childFragmentManager.commit {
                        add(R.id.fcv_detail_explore, detailExploreResultKeywordFragment)
                    }
                }
                detailExploreResultKeywordFragment
            }
        }

        val fragmentToHide = when (selectedFragmentTitle) {
            INFO -> detailExploreResultKeywordFragment
            KEYWORD -> detailExploreResultInfoFragment
        }

        childFragmentManager.commit {
            setReorderingAllowed(true)
            show(fragmentToShow)
            hide(fragmentToHide)
        }

        updateButtonColors(selectedFragmentTitle)
    }

    private fun updateButtonColors(selectedFragmentTitle: SelectedFragmentTitle) {
        val selectedColor = requireContext().getColor(R.color.primary_100_6A5DFD)
        val defaultColor = requireContext().getColor(R.color.gray_200_AEADB3)

        when (selectedFragmentTitle) {
            INFO -> {
                binding.apply {
                    tvDetailExploreInfoButton.setTextColor(selectedColor)
                    tvDetailExploreKeywordButton.setTextColor(defaultColor)
                    viewDetailExploreSelectedInfoTab.isVisible = true
                    viewDetailExploreSelectedKeywordTab.isVisible = false
                }
            }

            KEYWORD -> {
                binding.apply {
                    tvDetailExploreKeywordButton.setTextColor(selectedColor)
                    tvDetailExploreInfoButton.setTextColor(defaultColor)
                    viewDetailExploreSelectedInfoTab.isVisible = false
                    viewDetailExploreSelectedKeywordTab.isVisible = true
                }
            }
        }
    }

    private fun onBottomSheetExitButtonClick() {
        binding.ivDetailExploreExitButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupObserver() {
        detailExploreResultViewModel.isInfoChipSelected.observe(viewLifecycleOwner) { isVisible ->
            binding.ivDetailExploreInfoActiveDot.isVisible = isVisible
        }

        detailExploreResultViewModel.isKeywordChipSelected.observe(viewLifecycleOwner) { isVisible ->
            binding.ivDetailExploreKeywordActiveDot.isVisible = isVisible
        }
    }

    companion object {
        fun newInstance(): DetailExploreResultDialogBottomSheet = DetailExploreResultDialogBottomSheet()
    }
}
