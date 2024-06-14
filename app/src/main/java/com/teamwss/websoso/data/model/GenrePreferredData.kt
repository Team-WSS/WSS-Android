package com.teamwss.websoso.data.model


sealed class GenrePreferredData {

    data class GenreTop(
        val genreIcon: Int,
        val genreName: String,
        val genreCount: Int,
    ):GenrePreferredData()

    data class GenreBottom(
        val genreIcon: Int,
        val genreName: String,
        val genreCount: Int,
    ) : GenrePreferredData()
}

