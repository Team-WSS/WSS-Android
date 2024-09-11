package com.teamwss.websoso.ui.createFeed.adapter

import com.teamwss.websoso.ui.normalExplore.model.NormalExploreModel

sealed class SearchNovelItemType {

    data class Novels(val novel: NormalExploreModel.NovelModel) : SearchNovelItemType()

    data object Loading : SearchNovelItemType()

    enum class ItemType {
        NOVELS, LOADING;

        companion object {
            fun valueOf(ordinal: Int): ItemType = entries.find { it.ordinal == ordinal }
                ?: throw IllegalArgumentException()
        }
    }
}
