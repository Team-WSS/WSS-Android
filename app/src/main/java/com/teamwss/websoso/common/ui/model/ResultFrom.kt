package com.teamwss.websoso.common.ui.model

enum class ResultFrom {
    FeedDetailRemoved,
    FeedDetailBack,
    FeedDetailRefreshed,
    CreateFeed,
    Feed,
    BlockUser,
    ChangeUserInfo,
    ChangeProfileDisclosure,
    NormalExploreBack,
    NovelDetailBack,
    ProfileEditSuccess,
    ;

    val RESULT_OK: Int = ordinal
}
