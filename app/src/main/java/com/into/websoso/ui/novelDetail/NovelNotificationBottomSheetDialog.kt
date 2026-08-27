package com.into.websoso.ui.novelDetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseBottomSheetDialog
import com.into.websoso.core.common.util.collectWithLifecycle
import com.into.websoso.databinding.DialogNovelNotificationBinding
import com.into.websoso.ui.novelDetail.model.NovelNotificationUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelNotificationBottomSheetDialog : BaseBottomSheetDialog<DialogNovelNotificationBinding>(R.layout.dialog_novel_notification) {
    private val novelNotificationViewModel: NovelNotificationViewModel by viewModels()
    private val novelId: Long by lazy { arguments?.getLong(NOVEL_ID) ?: DEFAULT_NOVEL_ID }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        bindClickListener()
        setupObserver()
        novelNotificationViewModel.updateNovelNotificationSetting(novelId)
    }

    private fun bindClickListener() {
        binding.onCompletionToggleClick = {
            novelNotificationViewModel.updateCompletionNotificationEnabled(
                novelId = novelId,
                isEnabled = binding.scNovelNotificationCompletionToggle.isChecked.not(),
            )
        }
        binding.onHiatusReturnToggleClick = {
            novelNotificationViewModel.updateHiatusReturnNotificationEnabled(
                novelId = novelId,
                isEnabled = binding.scNovelNotificationHiatusReturnToggle.isChecked.not(),
            )
        }
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupObserver() {
        novelNotificationViewModel.novelNotificationUiState.collectWithLifecycle(viewLifecycleOwner) { uiState ->
            updateToggleState(uiState)
        }
    }

    private fun updateToggleState(uiState: NovelNotificationUiState) {
        binding.scNovelNotificationCompletionToggle.isChecked = uiState.isCompletionNotificationEnabled
        binding.scNovelNotificationHiatusReturnToggle.isChecked = uiState.isHiatusReturnNotificationEnabled

        binding.clNovelNotificationCompletion.isEnabled = uiState.isEditable
        binding.clNovelNotificationHiatusReturn.isEnabled = uiState.isEditable

        val contentAlpha = if (uiState.isError) DISABLED_ALPHA else DEFAULT_ALPHA
        binding.clNovelNotificationCompletion.alpha = contentAlpha
        binding.clNovelNotificationHiatusReturn.alpha = contentAlpha
    }

    companion object {
        const val NOVEL_NOTIFICATION_BOTTOM_SHEET_TAG = "NovelNotificationBottomSheetDialog"
        private const val NOVEL_ID = "NOVEL_ID"
        private const val DEFAULT_NOVEL_ID = 0L
        private const val DEFAULT_ALPHA = 1f
        private const val DISABLED_ALPHA = 0.4f

        fun newInstance(novelId: Long): NovelNotificationBottomSheetDialog =
            NovelNotificationBottomSheetDialog().apply {
                arguments = Bundle().apply { putLong(NOVEL_ID, novelId) }
            }
    }
}
