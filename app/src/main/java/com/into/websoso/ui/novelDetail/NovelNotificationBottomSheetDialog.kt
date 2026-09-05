package com.into.websoso.ui.novelDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.into.websoso.R
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.ui.novelDetail.component.NovelNotificationContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelNotificationBottomSheetDialog : BottomSheetDialogFragment() {
    private val novelNotificationViewModel: NovelNotificationViewModel by viewModels()
    private val novelId: Long by lazy { arguments?.getLong(NOVEL_ID) ?: DEFAULT_NOVEL_ID }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.WebsosoBottomSheetTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by novelNotificationViewModel.novelNotificationUiState.collectAsStateWithLifecycle()

                WebsosoTheme {
                    NovelNotificationContent(
                        uiState = uiState,
                        onCompletionToggleClick = {
                            novelNotificationViewModel.updateCompletionNotificationEnabled(
                                novelId = novelId,
                                isEnabled = uiState.isCompletionNotificationEnabled.not(),
                            )
                        },
                        onHiatusReturnToggleClick = {
                            novelNotificationViewModel.updateHiatusReturnNotificationEnabled(
                                novelId = novelId,
                                isEnabled = uiState.isHiatusReturnNotificationEnabled.not(),
                            )
                        },
                        onRetryClick = {
                            novelNotificationViewModel.updateNovelNotificationSetting(novelId)
                        },
                    )
                }
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        novelNotificationViewModel.updateNovelNotificationSetting(novelId)
    }

    companion object {
        const val NOVEL_NOTIFICATION_BOTTOM_SHEET_TAG = "NovelNotificationBottomSheetDialog"
        private const val NOVEL_ID = "NOVEL_ID"
        private const val DEFAULT_NOVEL_ID = 0L

        fun newInstance(novelId: Long): NovelNotificationBottomSheetDialog =
            NovelNotificationBottomSheetDialog().apply {
                arguments = Bundle().apply { putLong(NOVEL_ID, novelId) }
            }
    }
}
