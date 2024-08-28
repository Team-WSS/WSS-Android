package com.teamwss.websoso.ui.normalExplore.adapter

import com.teamwss.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

sealed class NormalExploreItemType {

    data class Header(val novelCount: Int) : NormalExploreItemType()

    data class Result(val novel: NovelModel) : NormalExploreItemType()

    enum class ItemType {
        HEADER, RESULT;

        companion object {
            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal }
                    ?: throw IllegalArgumentException()
        }
    }
}