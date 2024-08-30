package com.teamwss.websoso.ui.createFeed

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teamwss.websoso.R.layout.dialog_create_feed_search_novel
import com.teamwss.websoso.common.ui.base.BaseBottomSheetDialog
import com.teamwss.websoso.databinding.DialogCreateFeedSearchNovelBinding

class CreateFeedSearchNovelBottomSheetDialog :
    BaseBottomSheetDialog<DialogCreateFeedSearchNovelBinding>(dialog_create_feed_search_novel) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheet()
        binding.ivCreateFeedSearchNovelClose.setOnClickListener { dismiss() }
    }

    private fun setupBottomSheet() {
        (dialog as BottomSheetDialog).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            behavior.isHideable = false
            setCancelable(false)
        }
    }

    companion object {
        const val CREATE_FEED_SEARCH_NOVEL_TAG = "CREATE_FEED_SEARCH_NOVEL_TAG"

        fun newInstance(): CreateFeedSearchNovelBottomSheetDialog {
            return CreateFeedSearchNovelBottomSheetDialog()
        }
    }
}
