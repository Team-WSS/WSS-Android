package com.into.websoso.ui.novelDetail

import com.into.websoso.ui.novelRating.model.ReadStatus

interface NovelDetailClickListener {
    fun onNavigateBackClick()

    fun onShowMenuClick()

    fun onNavigateToNovelRatingClick(readStatus: ReadStatus)

    fun onNovelCoverClick(novelImageUrl: String)

    fun onNovelFeedWriteClick()

    fun onNovelInterestClick()
}
