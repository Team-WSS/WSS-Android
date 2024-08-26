package com.teamwss.websoso.ui.myPage.myLibrary.model

enum class AttractivePoints(val korean: String) {
    CHARACTER("캐릭터"),
    RELATIONSHIP("관계"),
    MATERIAL("소재");

    companion object {
        fun fromString(value: String): AttractivePoints? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}