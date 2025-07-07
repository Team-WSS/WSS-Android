package com.into.websoso.core.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> = if (value.isEmpty()) emptyList() else value.split(",")

    @TypeConverter
    fun listToString(list: List<String>): String = list.joinToString(",")
}
