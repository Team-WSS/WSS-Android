package com.into.websoso.domain.library.model

enum class AttractivePoints(
    val label: String,
    val key: String,
) {
    WORLDVIEW("세계관", "worldview"),
    MATERIAL("소재", "material"),
    CHARACTER("캐릭터", "character"),
    RELATIONSHIP("관계", "relationship"),
    VIBE("분위기", "vibe"),
    ;

    companion object {
        fun from(key: String): AttractivePoints =
            AttractivePoints.entries.find { attractivePoints -> attractivePoints.key == key }
                ?: WORLDVIEW
    }
}
