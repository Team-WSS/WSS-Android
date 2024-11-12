package com.teamwss.websoso.ui.main.myPage.myLibrary.model

import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.data.model.NovelPreferenceEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity

data class MyLibraryUiState(
    val isLoading: Boolean = false,
    val error: Boolean = false,
    val novelStats: UserNovelStatsEntity? = null,
    val topGenres: List<GenrePreferenceEntity> = emptyList(),
    val restGenres: List<GenrePreferenceEntity> = emptyList(),
    val novelPreferences: NovelPreferenceEntity? = null,
    val translatedAttractivePoints: List<String> = emptyList(),
    val isGenreListVisible: Boolean = false,
)