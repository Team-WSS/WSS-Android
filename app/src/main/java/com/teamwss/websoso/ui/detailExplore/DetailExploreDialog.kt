package com.teamwss.websoso.ui.detailExplore

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogDetailExploreBinding
import com.teamwss.websoso.ui.common.base.BindingBottomSheetDialog
import com.teamwss.websoso.ui.detailExplore.info.DetailExploreInfoFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreDialog :
    BindingBottomSheetDialog<DialogDetailExploreBinding>(R.layout.dialog_detail_explore) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
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

    private fun replaceDetailExploreFragment() {
        // TODO 정보/키워드 탭에 따른 fragment 수정
        childFragmentManager.beginTransaction()
            .replace(R.id.fcv_detail_explore, DetailExploreInfoFragment())
            .commit()
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