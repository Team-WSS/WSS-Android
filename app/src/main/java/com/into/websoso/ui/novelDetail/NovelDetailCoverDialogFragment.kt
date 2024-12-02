package com.into.websoso.ui.novelDetail

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.into.websoso.R
import com.into.websoso.common.ui.base.BaseDialogFragment
import com.into.websoso.databinding.DialogNovelDetailCoverBinding

class NovelDetailCoverDialogFragment :
    BaseDialogFragment<DialogNovelDetailCoverBinding>(R.layout.dialog_novel_detail_cover) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            coverImageUrl = requireArguments().getString(NOVEL_COVER_URL_KEY)
            onExitClick = { dismiss() }
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
        )
    }

    companion object {
        const val TAG = "NOVEL_COVER_DIALOG_FRAGMENT"
        private const val NOVEL_COVER_URL_KEY = "NOVEL_COVER_URL_KEY"

        fun newInstance(
            novelImageUrl: String,
        ): NovelDetailCoverDialogFragment = NovelDetailCoverDialogFragment().apply {
            arguments = Bundle().apply {
                putString(NOVEL_COVER_URL_KEY, novelImageUrl)
            }
        }
    }
}
