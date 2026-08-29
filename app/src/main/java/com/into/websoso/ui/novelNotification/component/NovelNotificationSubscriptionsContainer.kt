package com.into.websoso.ui.novelNotification.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.ui.novelNotification.model.NovelNotificationSubscriptionModel

private const val LOAD_THRESHOLD = 5

@Composable
fun NovelNotificationSubscriptionsContainer(
    subscriptions: List<NovelNotificationSubscriptionModel>,
    selectedNovelIds: Set<Long>,
    isEditing: Boolean,
    isLoadable: Boolean,
    updateSubscriptions: () -> Unit,
    onSubscriptionClick: (NovelNotificationSubscriptionModel) -> Unit,
    onSubscriptionSelect: (NovelNotificationSubscriptionModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, isLoadable, subscriptions.size) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo
                .lastOrNull()
                ?.index ?: 0
        }.collect { index ->
            if (index + LOAD_THRESHOLD >= subscriptions.size && isLoadable) {
                updateSubscriptions()
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
    ) {
        items(
            items = subscriptions,
            key = { it.subscriptionId },
        ) { subscription ->
            NovelNotificationSubscriptionItem(
                subscription = subscription,
                isEditing = isEditing,
                isSelected = subscription.novelId in selectedNovelIds,
                modifier = Modifier.clickableWithoutRipple {
                    when (isEditing) {
                        true -> onSubscriptionSelect(subscription)
                        false -> onSubscriptionClick(subscription)
                    }
                },
            )
        }
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

@Preview
@Composable
private fun NovelNotificationSubscriptionsContainerPreview() {
    WebsosoTheme {
        NovelNotificationSubscriptionsContainer(
            subscriptions = previewSubscriptions,
            selectedNovelIds = emptySet(),
            isEditing = false,
            isLoadable = false,
            updateSubscriptions = {},
            onSubscriptionClick = {},
            onSubscriptionSelect = {},
        )
    }
}

@Preview
@Composable
private fun NovelNotificationSubscriptionsContainerEditingPreview() {
    WebsosoTheme {
        NovelNotificationSubscriptionsContainer(
            subscriptions = previewSubscriptions,
            selectedNovelIds = setOf(0L, 2L),
            isEditing = true,
            isLoadable = false,
            updateSubscriptions = {},
            onSubscriptionClick = {},
            onSubscriptionSelect = {},
        )
    }
}

