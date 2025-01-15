package com.into.websoso.ui.notice

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NoticeScreen(
    viewModel: NoticeViewModel,
) {
    val uiState by viewModel.noticeUiState.collectAsStateWithLifecycle()
}
