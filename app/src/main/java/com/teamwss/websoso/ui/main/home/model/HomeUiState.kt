package com.teamwss.websoso.ui.main.home.model

import com.teamwss.websoso.data.model.PopularNovelsEntity.PopularNovelEntity

data class HomeUiState(
    val isLogin: Boolean = false,
    val loading: Boolean = true,
    val error: Boolean = false,
    val popularNovels: List<PopularNovelEntity> = listOf(
        PopularNovelEntity(
            novelId = 1L,
            title = "오늘의 웹소소 인기작품",
            nickname = "웹소소",
            feedContent = "",
            novelImage = "https://github.com/user-attachments/assets/4cc3aa71-6500-4936-b770-2f8c0204924f",
            avatarImage = "https://github.com/user-attachments/assets/76fc9681-1d3e-48e2-aed6-419ef4d5b3e6",
        )
    ),
)