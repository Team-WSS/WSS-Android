package com.teamwss.websoso.ui.main.feed.dialog

import android.os.Bundle
import android.view.View
import com.teamwss.websoso.R.layout
import com.teamwss.websoso.R.string.remove_popup_menu_description
import com.teamwss.websoso.R.string.remove_popup_menu_description_comment
import com.teamwss.websoso.R.string.remove_popup_menu_title
import com.teamwss.websoso.R.string.remove_popup_menu_title_comment
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.databinding.DialogRemovePopupMenuBinding
import com.teamwss.websoso.ui.main.feed.FeedFragment.FeedDialogClickListener
import com.teamwss.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_COMMENT
import com.teamwss.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_FEED

class FeedRemoveDialogFragment :
    BaseDialogFragment<DialogRemovePopupMenuBinding>(layout.dialog_remove_popup_menu) {
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val menuType: String? by lazy { arguments?.getString(MENU_TYPE) }
    private val onRemoveClick: FeedDialogClickListener by lazy {
        arguments?.getSerializable(EVENT) as FeedDialogClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (menuType) {
            REMOVE_FEED.name -> setupRemoveFeedView()
            REMOVE_COMMENT.name -> setupRemoveCommentView()
        }

        binding.tvRemovePopupMenuCancel.setOnClickListener { dismiss() }
        binding.tvRemovePopupMenuRemove.setOnClickListener {
            singleEventHandler.throttleFirst {
                onRemoveClick()
                dismiss()
            }
        }
        dialog?.setCanceledOnTouchOutside(false)
    }

    private fun setupRemoveCommentView() {
        binding.tvRemovePopupMenuTitle.text = getString(remove_popup_menu_title_comment)
        binding.tvRemovePopupMenuDescription.text = getString(remove_popup_menu_description_comment)
    }

    private fun setupRemoveFeedView() {
        binding.tvRemovePopupMenuTitle.text = getString(remove_popup_menu_title)
        binding.tvRemovePopupMenuDescription.text = getString(remove_popup_menu_description)
    }

    companion object {
        private const val EVENT = "EVENT"
        private const val MENU_TYPE = "MENU_TYPE"
        const val TAG = "FeedRemoveDialogFragment"

        fun newInstance(
            menuType: String,
            event: FeedDialogClickListener,
        ): FeedRemoveDialogFragment =
            FeedRemoveDialogFragment().also {
                it.arguments = Bundle().apply {
                    putString(MENU_TYPE, menuType)
                    putSerializable(EVENT, event)
                }
            }
    }
}

enum class RemoveMenuType {
    REMOVE_FEED, REMOVE_COMMENT,
}
