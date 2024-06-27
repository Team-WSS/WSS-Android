package com.teamwss.websoso.ui.detailExplore

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogDetailExploreBinding
import com.teamwss.websoso.ui.common.base.BindingBottomSheetDialog
import com.teamwss.websoso.ui.detailExplore.info.DetailExploreInfoFragment

class DetailExploreDialog :
    BindingBottomSheetDialog<DialogDetailExploreBinding>(R.layout.dialog_detail_explore) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
        setupFragmentContainer()
        onBottomSheetExitButtonClick()
    }

    private fun setupBottomSheet() {
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.isDraggable = false
        (dialog as BottomSheetDialog).behavior.isHideable = false
        (dialog as BottomSheetDialog).setCancelable(false)
    }

    private fun setupFragmentContainer() {
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
}