package com.into.websoso.ui.detailExplore

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
import com.into.websoso.ui.detailExplore.info.DetailExploreInfoFragment
import com.into.websoso.ui.detailExplore.keyword.DetailExploreKeywordFragment
import com.into.websoso.ui.detailExplore.model.SelectedFragmentTitle
import com.into.websoso.ui.detailExplore.model.SelectedFragmentTitle.INFO
import com.into.websoso.ui.detailExplore.model.SelectedFragmentTitle.KEYWORD
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreDialogBottomSheet : BaseBottomSheetDialog<DialogDetailExploreBinding>(R.layout.dialog_detail_explore) {
    private val detailExploreInfoFragment: DetailExploreInfoFragment by lazy { DetailExploreInfoFragment() }
    private val detailExploreKeywordFragment: DetailExploreKeywordFragment by lazy { DetailExploreKeywordFragment() }
    private val detailExploreViewModel: DetailExploreViewModel by activityViewModels()
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
                detailExploreInfoFragment,
            )
        }
    }

    private fun onReplaceFragmentButtonClick() {
        binding.tvDetailExploreInfoButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                switchFragment(INFO)
                detailExploreViewModel.updateIsSearchKeywordProceeding(false)
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
            INFO -> detailExploreInfoFragment
            KEYWORD -> {
                if (childFragmentManager.findFragmentById(R.id.fcv_detail_explore) !is DetailExploreKeywordFragment) {
                    childFragmentManager.commit {
                        add(R.id.fcv_detail_explore, detailExploreKeywordFragment)
                    }
                }
                detailExploreKeywordFragment
            }
        }

        val fragmentToHide = when (selectedFragmentTitle) {
            INFO -> detailExploreKeywordFragment
            KEYWORD -> detailExploreInfoFragment
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
            detailExploreViewModel.updateSelectedInfoValueClear()
            detailExploreViewModel.updateSelectedKeywordValueClear()
            dismiss()
        }
    }

    private fun setupObserver() {
        detailExploreViewModel.isInfoChipSelected.observe(viewLifecycleOwner) { isVisible ->
            binding.ivDetailExploreInfoActiveDot.isVisible = isVisible
        }

        detailExploreViewModel.isKeywordChipSelected.observe(viewLifecycleOwner) { isVisible ->
            binding.ivDetailExploreKeywordActiveDot.isVisible = isVisible
        }
    }

    override fun onPause() {
        detailExploreViewModel.updateSelectedInfoValueClear()
        detailExploreViewModel.updateSelectedKeywordValueClear()
        dismiss()
        super.onPause()
    }

    companion object {
        fun newInstance(): DetailExploreDialogBottomSheet = DetailExploreDialogBottomSheet()
    }
}
