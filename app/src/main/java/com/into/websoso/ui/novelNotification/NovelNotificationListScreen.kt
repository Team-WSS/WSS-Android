package com.into.websoso.ui.novelNotification

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.ui.novelNotification.component.NovelNotificationDeleteDialog
import com.into.websoso.ui.novelNotification.component.NovelNotificationEmptyView
import com.into.websoso.ui.novelNotification.component.NovelNotificationListAppBar
import com.into.websoso.ui.novelNotification.component.NovelNotificationSubscriptionsContainer
import com.into.websoso.ui.novelNotification.model.NovelNotificationSubscriptionModel

@Composable
fun NovelNotificationListScreen(
    viewModel: NovelNotificationListViewModel,
    onSubscriptionClick: (NovelNotificationSubscriptionModel) -> Unit,
    onExploreClick: () -> Unit,
    onBackButtonClick: () -> Unit,
) {
    val uiState by viewModel.novelNotificationListUiState.collectAsStateWithLifecycle()

    BackHandler {
        when (uiState.isEditing) {
            true -> viewModel.updateEditing(false)
            false -> onBackButtonClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        NovelNotificationListAppBar(
            notificationType = uiState.notificationType,
            isEditing = uiState.isEditing,
            isDeletable = uiState.isDeletable,
            isActionVisible = uiState.isEmpty.not(),
            onBackButtonClick = onBackButtonClick,
            onEditButtonClick = { viewModel.updateEditing(true) },
            onDeleteButtonClick = { viewModel.updateDeleteDialogVisibility(true) },
        )
        when {
            uiState.isEmpty -> NovelNotificationEmptyView(onExploreClick = onExploreClick)
            else -> NovelNotificationSubscriptionsContainer(
                subscriptions = uiState.subscriptions,
                selectedNovelIds = uiState.selectedNovelIds,
                isEditing = uiState.isEditing,
                isLoadable = uiState.isLoadable,
                updateSubscriptions = viewModel::updateSubscriptions,
                onSubscriptionClick = onSubscriptionClick,
                onSubscriptionSelect = { viewModel.updateSelectedNovel(it.novelId) },
            )
        }
    }

    if (uiState.isDeleteDialogVisible) {
        NovelNotificationDeleteDialog(
            selectedSubscriptions = uiState.selectedSubscriptions,
            onCancelClick = { viewModel.updateDeleteDialogVisibility(false) },
            onConfirmClick = viewModel::deleteSelectedSubscriptions,
        )
    }
}
