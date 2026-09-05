package com.into.websoso.ui.novelDetail.model

data class NovelNotificationUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isCompletionNotificationEnabled: Boolean = false,
    val isHiatusReturnNotificationEnabled: Boolean = false,
) {
    // 초기 조회 전 기본값(false)을 기준으로 저장하면 건드리지 않은 알림까지 해제되므로 조회 성공 후에만 변경을 허용한다
    val isEditable: Boolean get() = isLoading.not() && isError.not()
}
