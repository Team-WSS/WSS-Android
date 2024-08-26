package com.teamwss.websoso.ui.detailExplore

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogDetailExploreBinding
import com.teamwss.websoso.common.ui.base.BaseBottomSheetDialog
import com.teamwss.websoso.ui.detailExplore.info.DetailExploreInfoFragment
import com.teamwss.websoso.ui.detailExplore.keyword.DetailExploreKeywordFragment
import com.teamwss.websoso.ui.detailExplore.model.SelectedFragmentTitle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreDialogBottomSheet :
    BaseBottomSheetDialog<DialogDetailExploreBinding>(R.layout.dialog_detail_explore) {
    private val detailExploreInfoFragment: DetailExploreInfoFragment by lazy { DetailExploreInfoFragment() }
    private val detailExploreKeywordFragment: DetailExploreKeywordFragment by lazy { DetailExploreKeywordFragment() }

    private val detailExploreViewModel: DetailExploreViewModel by activityViewModels()

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
        childFragmentManager.beginTransaction()
            .add(R.id.fcv_detail_explore, detailExploreInfoFragment)
            .add(R.id.fcv_detail_explore, detailExploreKeywordFragment)
            .hide(detailExploreKeywordFragment)
            .commit()
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
            SelectedFragmentTitle.INFO -> detailExploreInfoFragment
            SelectedFragmentTitle.KEYWORD -> detailExploreKeywordFragment
        }

        childFragmentManager.beginTransaction().apply {
            hide(detailExploreInfoFragment)
            hide(detailExploreKeywordFragment)
            show(fragmentToShow)
            commit()
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
        detailExploreViewModel.isInfoChipSelected.observe(viewLifecycleOwner) { isVisible ->
            binding.ivDetailExploreInfoActiveDot.isVisible = isVisible
        }
    }

    companion object {

        fun newInstance(): DetailExploreDialogBottomSheet {
            return DetailExploreDialogBottomSheet()
        }
    }
}