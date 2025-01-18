package com.into.websoso.ui.novelInfo.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.ui.novelInfo.model.PlatformModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NovelInfoPlatformsContainer(
    platforms: List<PlatformModel>,
    navigateToPlatform: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        platforms.forEach { platform ->
            PlatformCard(
                platformImage = platform.platformImage,
                onClick = { navigateToPlatform(platform.platformUrl) },
            )
        }
    }
}

@Preview
@Composable
private fun NovelInfoPlatformsContainerPreview() {
    NovelInfoPlatformsContainer(
        platforms = listOf(
            PlatformModel(
                platformName = "네이버시리즈",
                platformImage = "/platform/naver-series",
                platformUrl = "https://series.naver.com/novel/detail.series?productNo=9378942",
            ),
            PlatformModel(
                platformName = "카카오페이지",
                platformImage = "/platform/kakao-page",
                platformUrl = "https://page.kakao.com/content/46753602",
            ),
        ),
        navigateToPlatform = {},
    )
}
