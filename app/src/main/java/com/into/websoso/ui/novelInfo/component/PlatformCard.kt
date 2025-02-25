package com.into.websoso.ui.novelInfo.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.ui.component.AdaptationImage
import com.into.websoso.core.common.util.clickableWithoutRipple

@Composable
fun PlatformCard(
    platformImage: String,
    onClick: () -> Unit,
) {
    AdaptationImage(
        imageUrl = platformImage,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .width(46.dp)
            .height(48.dp)
            .border(
                shape = RoundedCornerShape(12.dp),
                width = 1.dp,
                color = Color.Transparent,
            ).clickableWithoutRipple { onClick() },
    )
}

@Preview
@Composable
private fun PlatformCardPreview() {
    PlatformCard(
        platformImage = "/platform/naver-series",
        onClick = {},
    )
}
