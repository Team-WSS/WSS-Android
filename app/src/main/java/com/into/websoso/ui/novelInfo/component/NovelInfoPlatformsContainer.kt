package com.into.websoso.ui.novelInfo.component

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.into.websoso.common.util.getS3ImageUrl
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
