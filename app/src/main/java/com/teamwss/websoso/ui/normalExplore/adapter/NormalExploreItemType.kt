package com.teamwss.websoso.ui.normalExplore.adapter

import com.teamwss.websoso.data.model.NormalExploreEntity.NovelEntity

sealed class NormalExploreItemType {

    data class Header(val novelCount: Int) : NormalExploreItemType()

    data class Result(val novel: NovelEntity) : NormalExploreItemType()

    enum class ItemType {
        HEADER, RESULT;

        companion object {
            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal }
                    ?: throw IllegalArgumentException()
        }
    }
}