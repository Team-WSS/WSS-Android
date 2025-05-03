package com.into.websoso.ui.main.myPage.myLibrary.model

enum class AttractivePoints(
    val korean: String,
) {
    CHARACTER("캐릭터"),
    RELATIONSHIP("관계"),
    WORLDVIEW("세계관"),
    VIBE("분위기"),
    MATERIAL("소재"),
    ;

    companion object {
        fun fromString(value: String): AttractivePoints? = values().find { it.name.equals(value, ignoreCase = true) }
    }
}
