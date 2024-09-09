package com.teamwss.websoso.ui.novelDetail

import com.teamwss.websoso.ui.novelRating.model.ReadStatus

interface NovelDetailClickListener {
    fun onNavigateBackClick()

    fun onShowMenuClick()

    fun onNavigateToNovelRatingClick(readStatus: ReadStatus)

    fun onNovelCoverClick(novelImageUrl: String)
}
