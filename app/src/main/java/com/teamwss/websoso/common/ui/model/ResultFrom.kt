package com.teamwss.websoso.common.ui.model

enum class ResultFrom {
    BlockUser,
    FeedDetailBack,
    FeedDetailRemoved,
    FeedDetailRefreshed,
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
    ;

    val RESULT_OK: Int = ordinal + 1
}
