package com.into.websoso.ui.novelNotification.model

import com.into.websoso.domain.model.NovelNotificationSubscriptions.Companion.DEFAULT_SUBSCRIPTION_ID
import com.into.websoso.domain.model.NovelNotificationType
import com.into.websoso.domain.model.NovelNotificationType.COMPLETION

data class NovelNotificationListUiState(
    val notificationType: NovelNotificationType = COMPLETION,
    val isLoadable: Boolean = true,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isInitialLoaded: Boolean = false,
    val isEditing: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
    val lastSubscriptionId: Long = DEFAULT_SUBSCRIPTION_ID,
    val selectedNovelIds: Set<Long> = emptySet(),
    val subscriptions: List<NovelNotificationSubscriptionModel> = emptyList(),
) {
    // 조회 실패도 isInitialLoaded를 true로 만들기 때문에, 오류와 로딩을 빼지 않으면
    // 네트워크 오류나 재시도 중에 '등록한 작품이 없어요'가 잘못 노출된다
    val isEmpty: Boolean get() = isInitialLoaded && isLoading.not() && isError.not() && subscriptions.isEmpty()

    // 이미 불러온 항목이 있으면 다음 페이지 실패로 목록을 지우지 않고 그대로 둔다
    val isErrorVisible: Boolean get() = isError && subscriptions.isEmpty()

    val isActionVisible: Boolean get() = subscriptions.isNotEmpty()

    val isDeletable: Boolean get() = selectedNovelIds.isNotEmpty()

    // 삭제 알럿은 '처음 선택한 작품'을 기준으로 문구를 구성하므로 목록 순서가 아닌 선택 순서를 유지한다
    val selectedSubscriptions: List<NovelNotificationSubscriptionModel>
        get() = selectedNovelIds.mapNotNull { novelId ->
            subscriptions.find { it.novelId == novelId }
        }
}
