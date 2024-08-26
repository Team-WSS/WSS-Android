package com.teamwss.websoso.ui.changeUserInfo.model

enum class Gender(val genderCode: String, val displayName: String) {
    MALE("M", "남자"),
    FEMALE("F", "여자");

    companion object {

        fun from(code: String): Gender =
            entries.find { it.genderCode == code } ?: throw IllegalArgumentException()
    }
}