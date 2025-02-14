package com.into.websoso.core.common.ui.model

enum class ResultFrom {
    BlockUser,
    FeedDetailBack,
    FeedDetailRemoved,
    FeedDetailRefreshed,
    FeedDetailError,
    Feed,
    CreateFeed,
    ChangeUserInfo,
    ChangeProfileDisclosure,
    NormalExploreBack,
    NovelDetailBack,
    ProfileEditSuccess,
    OtherUserProfileBack,
    NovelRating,
    WithdrawUser,
    Notification,
    ;

    val RESULT_OK: Int = ordinal + 1
}
