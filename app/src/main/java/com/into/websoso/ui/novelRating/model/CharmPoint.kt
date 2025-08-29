package com.into.websoso.ui.novelRating.model

enum class CharmPoint(
    val value: String,
    val title: String,
) {
    WORLDVIEW("worldview", "세계관"),
    VIBE("vibe", "분위기"),
    MATERIAL("material", "소재"),
    CHARACTER("character", "캐릭터"),
    RELATIONSHIP("relationship", "관계"),
    ;

    companion object {
        fun String.toWrappedCharmPoint(): List<CharmPoint> {
            return split(",").map {
                entries.find { charmPoint -> charmPoint.title == it } ?: return emptyList()
            }
        }

        fun String.toFormattedCharmPoint(): String {
            entries.forEach { charmPoint ->
                if (charmPoint.value == this) return charmPoint.title
            }
            return ""
        }
    }
}
