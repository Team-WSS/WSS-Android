package com.teamwss.websoso.ui.normalExplore.adapter

import com.teamwss.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

sealed class NormalExploreItemType {

    data class Header(val novelCount: Long) : NormalExploreItemType()

    data class Result(val novel: NovelModel) : NormalExploreItemType()

    data object Loading : NormalExploreItemType()

    enum class ItemType {
        HEADER, RESULT, LOADING;

        companion object {
            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal }
                    ?: throw IllegalArgumentException()
        }
    }
}