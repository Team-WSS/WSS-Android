package com.teamwss.websoso.ui.detailExplore

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogDetailExploreBinding
import com.teamwss.websoso.ui.common.base.BindingBottomSheetDialog
import com.teamwss.websoso.ui.detailExplore.info.DetailExploreInfoFragment
import com.teamwss.websoso.ui.detailExplore.keyword.DetailExploreKeywordFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreDialog :
    BindingBottomSheetDialog<DialogDetailExploreBinding>(R.layout.dialog_detail_explore) {
    private lateinit var detailExploreInfoFragment: DetailExploreInfoFragment
    private lateinit var detailExploreKeywordFragment: DetailExploreKeywordFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
        initDetailExploreFragment()
        replaceDetailExploreFragment()
        onBottomSheetExitButtonClick()
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
        detailExploreInfoFragment = DetailExploreInfoFragment()
        detailExploreKeywordFragment = DetailExploreKeywordFragment()

        childFragmentManager.beginTransaction()
            .add(R.id.fcv_detail_explore, detailExploreInfoFragment)
            .add(R.id.fcv_detail_explore, detailExploreKeywordFragment)
            .hide(detailExploreKeywordFragment)
            .commit()
    }

    private fun replaceDetailExploreFragment() {
        
        binding.tvDetailExploreInfoButton.setOnClickListener {
            childFragmentManager.beginTransaction()
                .hide(detailExploreKeywordFragment)
                .show(detailExploreInfoFragment)
                .commit()
        }

        binding.tvDetailExploreKeywordButton.setOnClickListener {
            childFragmentManager.beginTransaction()
                .hide(detailExploreInfoFragment)
                .show(detailExploreKeywordFragment)
                .commit()
        }
    }

    private fun onBottomSheetExitButtonClick() {
        binding.ivDetailExploreExitButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {

        fun newInstance(): DetailExploreDialog {
            return DetailExploreDialog()
        }
    }
}