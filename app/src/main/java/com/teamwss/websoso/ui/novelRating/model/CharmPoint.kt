package com.teamwss.websoso.ui.novelRating.model

enum class CharmPoint(val value: String, val title: String) {
    WORLDVIEW("worldview", "세계관"),
    MATERIAL("material", "소재"),
    CHARACTER("character", "캐릭터"),
    RELATIONSHIP("relationship", "관계"),
    VIBE("vibe", "분위기");

    companion object {
        fun String.toWrappedCharmPoint(): List<CharmPoint> {
            return split(",").map {
                values().find { charmPoint -> charmPoint.title == it }
                    ?: throw IllegalArgumentException()
            }
        }
    }
}