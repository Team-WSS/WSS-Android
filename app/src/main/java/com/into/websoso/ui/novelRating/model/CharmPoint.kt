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
    WRITINGSKILL("writingskill", "필력"),
    ;

    companion object {
        fun String.toWrappedCharmPoint(): List<CharmPoint> =
            split(",").map { rawTitle ->
                val trimmedTitle = rawTitle.trim()
                entries.find { charmPoint -> charmPoint.title == trimmedTitle }
                    ?: throw IllegalArgumentException("존재하지 않는 매력포인트입니다: $rawTitle")
            }

        fun String.toFormattedCharmPoint(): String {
            entries.forEach { charmPoint ->
                if (charmPoint.value == this) return charmPoint.title
            }
            return ""
        }
    }
}
