package com.into.websoso.ui.normalExplore.adapter

import com.into.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

sealed class NormalExploreItemType {

    data class Header(val novelCount: Long) : NormalExploreItemType()

    data class Novels(val novel: NovelModel) : NormalExploreItemType()

    data object Loading : NormalExploreItemType()

    enum class ItemType {
        HEADER, NOVELS, LOADING;

        companion object {
            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal }
                    ?: throw IllegalArgumentException()
        }
    }
}