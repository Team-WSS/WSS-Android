package com.teamwss.websoso.ui.detailExploreResult

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseBottomSheetDialog
import com.teamwss.websoso.databinding.DialogDetailExploreBinding
import com.teamwss.websoso.ui.detailExplore.model.SelectedFragmentTitle

class DetailExploreResultDialogBottomSheet :
    BaseBottomSheetDialog<DialogDetailExploreBinding>(R.layout.dialog_detail_explore) {
    private val detailExploreResultInfoFragment: DetailExploreResultInfoFragment by lazy { DetailExploreResultInfoFragment() }
    private val detailExploreResultKeywordFragment: DetailExploreResultKeywordFragment by lazy { DetailExploreResultKeywordFragment() }
    private val detailExploreResultViewModel: DetailExploreResultViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            switchFragment(SelectedFragmentTitle.INFO)
        }

        binding.tvDetailExploreKeywordButton.setOnClickListener {
            switchFragment(SelectedFragmentTitle.KEYWORD)
        }
    }

    private fun switchFragment(selectedFragmentTitle: SelectedFragmentTitle) {
        val fragmentToShow = when (selectedFragmentTitle) {
            SelectedFragmentTitle.INFO -> detailExploreResultInfoFragment
            SelectedFragmentTitle.KEYWORD -> {
                if (childFragmentManager.findFragmentById(R.id.fcv_detail_explore) !is DetailExploreResultKeywordFragment) {
                    childFragmentManager.commit {
                        add(R.id.fcv_detail_explore, detailExploreResultKeywordFragment)
                    }
                }
                detailExploreResultKeywordFragment
            }
        }

        val fragmentToHide = when (selectedFragmentTitle) {
            SelectedFragmentTitle.INFO -> detailExploreResultKeywordFragment
            SelectedFragmentTitle.KEYWORD -> detailExploreResultInfoFragment
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
            SelectedFragmentTitle.INFO -> {
                binding.apply {
                    tvDetailExploreInfoButton.setTextColor(selectedColor)
                    tvDetailExploreKeywordButton.setTextColor(defaultColor)
                    viewDetailExploreSelectedInfoTab.isVisible = true
                    viewDetailExploreSelectedKeywordTab.isVisible = false
                }
            }

            SelectedFragmentTitle.KEYWORD -> {
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

        fun newInstance(): DetailExploreResultDialogBottomSheet {
            return DetailExploreResultDialogBottomSheet()
        }
    }
}