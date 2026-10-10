package com.into.websoso.domain.library.model

data class Genres(
    val value: Map<Genre, Boolean> = Genre.entries.associateWith { false },
) {
    val isSelected: Boolean
        get() = value.any { it.value }

    val selectedGenres: List<Genre>
        get() = value.filterValues { it }.keys.toList()

    val selectedLabels: List<String>
        get() = selectedGenres.map { it.label }

    val selectedKeys: List<String>
        get() = selectedGenres.map { it.key }

    operator fun get(genre: Genre): Boolean = value[genre] ?: false

    fun set(genre: Genre): Genres {
        if (!value.containsKey(genre)) return this
        val updatedGenre = value.toMutableMap().apply {
            this[genre] = !(this[genre] ?: false)
        }
        return this.copy(value = updatedGenre)
    }

    companion object {
        fun List<String>.toGenres(): Genres {
            val enabledSet = mapNotNull(Genre::from).toSet()
            val mapped = Genre.entries.associateWith { it in enabledSet }
            return Genres(mapped)
        }
    }
}
