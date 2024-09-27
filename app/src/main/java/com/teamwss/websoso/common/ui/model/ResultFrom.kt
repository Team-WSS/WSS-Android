package com.teamwss.websoso.common.ui.model

enum class ResultFrom(private val resultCode: Int) {
    FeedDetailBack(1),
    FeedDetailRemoved(2),
    CreateFeed(3),
    BlockUser(4),
    ChangeUserInfo(5),
    ChangeProfileDisclosure(6),
    NormalExploreBack(7),
    NovelDetailBack(8),
    ProfileEditSuccess(9),
    NovelRating(10),
    ;

    val RESULT_OK: Int = resultCode
}
