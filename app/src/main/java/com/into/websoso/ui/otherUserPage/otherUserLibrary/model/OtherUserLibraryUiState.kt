package com.into.websoso.ui.otherUserPage.otherUserLibrary.model

import com.into.websoso.data.model.GenrePreferenceEntity
import com.into.websoso.data.model.NovelPreferenceEntity
import com.into.websoso.data.model.UserNovelStatsEntity

data class OtherUserLibraryUiState(
    val isLoading: Boolean = false,
    val error: Boolean = false,
    val novelStats: UserNovelStatsEntity? = null,
    val topGenres: List<GenrePreferenceEntity> = emptyList(),
    val restGenres: List<GenrePreferenceEntity> = emptyList(),
    val novelPreferences: NovelPreferenceEntity? = null,
    val translatedAttractivePoints: List<String> = emptyList(),
    val isGenreListVisible: Boolean = false,
)
