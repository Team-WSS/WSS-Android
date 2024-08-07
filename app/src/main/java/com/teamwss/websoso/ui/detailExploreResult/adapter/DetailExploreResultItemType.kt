package com.teamwss.websoso.ui.detailExploreResult.adapter

import com.teamwss.websoso.data.model.ExploreResultEntity.NovelEntity

sealed class DetailExploreResultItemType {

    data class Header(val novelCount: Int) : DetailExploreResultItemType()

    data class Result(val novel: NovelEntity) : DetailExploreResultItemType()

    enum class ItemType {
        HEADER, RESULT;

        companion object {
            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal }
                    ?: throw IllegalArgumentException()
        }
    }
}