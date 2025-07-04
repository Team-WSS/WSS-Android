package com.into.websoso.feature.library.model

enum class AttractivePoint(
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
        fun from(key: String?): AttractivePoint? = entries.find { it.key == key }
    }
}
