package com.teamwss.websoso.ui.detailExploreResult.adapter

import com.teamwss.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

sealed class DetailExploreResultItemType {

    data class Header(val novelCount: Long) : DetailExploreResultItemType()

    data class Result(val novel: NovelModel) : DetailExploreResultItemType()

    data object Loading : DetailExploreResultItemType()

    enum class ItemType {
        HEADER, RESULT, LOADING;

        companion object {
            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal }
                    ?: throw IllegalArgumentException()
        }
    }
}