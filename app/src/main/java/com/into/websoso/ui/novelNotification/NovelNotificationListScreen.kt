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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.ui.novelNotification.component.NovelNotificationDeleteDialog
import com.into.websoso.ui.novelNotification.component.NovelNotificationEmptyView
import com.into.websoso.ui.novelNotification.component.NovelNotificationListAppBar
import com.into.websoso.ui.novelNotification.component.NovelNotificationSubscriptionsContainer
import com.into.websoso.ui.novelNotification.model.NovelNotificationListUiState
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

    NovelNotificationListScreen(
        uiState = uiState,
        updateSubscriptions = viewModel::updateSubscriptions,
        onEditButtonClick = { viewModel.updateEditing(true) },
        onDeleteButtonClick = { viewModel.updateDeleteDialogVisibility(true) },
        onSubscriptionSelect = { viewModel.updateSelectedNovel(it.novelId) },
        onDeleteDialogCancelClick = { viewModel.updateDeleteDialogVisibility(false) },
        onDeleteDialogConfirmClick = viewModel::deleteSelectedSubscriptions,
        onSubscriptionClick = onSubscriptionClick,
        onExploreClick = onExploreClick,
        onBackButtonClick = onBackButtonClick,
    )
}

@Composable
private fun NovelNotificationListScreen(
    uiState: NovelNotificationListUiState,
    updateSubscriptions: () -> Unit,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onSubscriptionSelect: (NovelNotificationSubscriptionModel) -> Unit,
    onDeleteDialogCancelClick: () -> Unit,
    onDeleteDialogConfirmClick: () -> Unit,
    onSubscriptionClick: (NovelNotificationSubscriptionModel) -> Unit,
    onExploreClick: () -> Unit,
    onBackButtonClick: () -> Unit,
) {
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
            onEditButtonClick = onEditButtonClick,
            onDeleteButtonClick = onDeleteButtonClick,
        )
        when {
            uiState.isEmpty -> NovelNotificationEmptyView(onExploreClick = onExploreClick)

            else -> NovelNotificationSubscriptionsContainer(
                subscriptions = uiState.subscriptions,
                selectedNovelIds = uiState.selectedNovelIds,
                isEditing = uiState.isEditing,
                isLoadable = uiState.isLoadable,
                updateSubscriptions = updateSubscriptions,
                onSubscriptionClick = onSubscriptionClick,
                onSubscriptionSelect = onSubscriptionSelect,
            )
        }
    }

    if (uiState.isDeleteDialogVisible) {
        NovelNotificationDeleteDialog(
            selectedSubscriptions = uiState.selectedSubscriptions,
            onCancelClick = onDeleteDialogCancelClick,
            onConfirmClick = onDeleteDialogConfirmClick,
        )
    }
}

private val previewSubscriptions = List(3) { index ->
    NovelNotificationSubscriptionModel(
        subscriptionId = index.toLong(),
        novelId = index.toLong(),
        novelTitle = "여주인공의 이해를 돕기 위하여 $index",
        novelAuthor = "이보라",
        novelImage = "",
        registeredDate = "2026.07.04",
    )
}

@Composable
private fun NovelNotificationListScreenPreviewContent(uiState: NovelNotificationListUiState) {
    WebsosoTheme {
        NovelNotificationListScreen(
            uiState = uiState,
            updateSubscriptions = {},
            onEditButtonClick = {},
            onDeleteButtonClick = {},
            onSubscriptionSelect = {},
            onDeleteDialogCancelClick = {},
            onDeleteDialogConfirmClick = {},
            onSubscriptionClick = {},
            onExploreClick = {},
            onBackButtonClick = {},
        )
    }
}

@Preview
@Composable
private fun NovelNotificationListScreenPreview() {
    NovelNotificationListScreenPreviewContent(
        uiState = NovelNotificationListUiState(
            isInitialLoaded = true,
            isLoadable = false,
            subscriptions = previewSubscriptions,
        ),
    )
}

@Preview
@Composable
private fun NovelNotificationListScreenEditingPreview() {
    NovelNotificationListScreenPreviewContent(
        uiState = NovelNotificationListUiState(
            isInitialLoaded = true,
            isLoadable = false,
            isEditing = true,
            selectedNovelIds = setOf(0L, 2L),
            subscriptions = previewSubscriptions,
        ),
    )
}

@Preview
@Composable
private fun NovelNotificationListScreenEmptyPreview() {
    NovelNotificationListScreenPreviewContent(
        uiState = NovelNotificationListUiState(
            isInitialLoaded = true,
            isLoadable = false,
        ),
    )
}

@Preview
@Composable
private fun NovelNotificationListScreenDeleteDialogPreview() {
    NovelNotificationListScreenPreviewContent(
        uiState = NovelNotificationListUiState(
            isInitialLoaded = true,
            isLoadable = false,
            isEditing = true,
            isDeleteDialogVisible = true,
            selectedNovelIds = setOf(0L, 2L),
            subscriptions = previewSubscriptions,
        ),
    )
}
