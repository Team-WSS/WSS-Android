package com.into.websoso.ui.main.myPage.myLibrary.model

import com.into.websoso.data.model.GenrePreferenceEntity
import com.into.websoso.data.model.NovelPreferenceEntity
import com.into.websoso.data.model.UserNovelStatsEntity

data class MyLibraryUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val novelStats: UserNovelStatsEntity? = null,
    val topGenres: List<GenrePreferenceEntity> = emptyList(),
    val restGenres: List<GenrePreferenceEntity> = emptyList(),
    val novelPreferences: NovelPreferenceEntity? = null,
    val translatedAttractivePoints: List<String> = emptyList(),
    val isGenreListVisible: Boolean = false,
)