package com.teamwss.websoso.data.model

sealed class PreferredGenreData {
    data class GenreTop(
        val genreIcon: String,
        val genreName: String,
        val genreCount: Int,
        ) : PreferredGenreData()

    data class GenreBottom(
        val genreIcon: String,
        val genreName: String,
        val genreCount: Int,
    ) : PreferredGenreData()
}
