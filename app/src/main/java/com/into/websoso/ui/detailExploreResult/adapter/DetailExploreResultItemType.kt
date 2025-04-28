package com.into.websoso.ui.detailExploreResult.adapter

import com.into.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

sealed class DetailExploreResultItemType {
    data class Header(
        val novelCount: Long,
    ) : DetailExploreResultItemType()

    data class Novels(
        val novel: NovelModel,
    ) : DetailExploreResultItemType()

    data object Loading : DetailExploreResultItemType()

    enum class ItemType {
        HEADER,
        NOVELS,
        LOADING,
        ;

        companion object {
            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal }
                    ?: throw IllegalArgumentException()
        }
    }
}
