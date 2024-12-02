package com.into.websoso.ui.novelInfo.model

data class NovelInfoUiState(
    val novelInfoModel: NovelInfoUiModel = NovelInfoUiModel(),
    val keywords: List<KeywordModel> = emptyList(),
    val platforms: List<PlatformModel> = emptyList(),
    val expandTextModel: ExpandTextUiModel = ExpandTextUiModel(),
    val loading: Boolean = false,
    val error: Boolean = false,
    val isKeywordsExist: Boolean = false,
) {

    fun getNaverPlatform(): PlatformModel {
        return platforms.firstOrNull { it.platform == Platform.NAVER_SERIES } ?: PlatformModel()
    }

    fun getKakaoPlatform(): PlatformModel {
        return platforms.firstOrNull { it.platform == Platform.KAKAO_PAGE } ?: PlatformModel()
    }
}
