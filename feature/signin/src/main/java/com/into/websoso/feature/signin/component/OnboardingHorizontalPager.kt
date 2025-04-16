package com.into.websoso.feature.signin.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R

internal val Onboarding_Images = arrayOf(
    R.drawable.img_login_1,
    R.drawable.img_login_2,
    R.drawable.img_login_3,
    R.drawable.img_login_4,
)

@Composable
internal fun OnboardingHorizontalPager(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
    ) { pageIndex ->
        Image(
            painter = painterResource(id = Onboarding_Images[pageIndex]),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingHorizontalPagerPreview() {
    WebsosoTheme {
        val pagerState = rememberPagerState { 4 }

        OnboardingHorizontalPager(pagerState = pagerState)
    }
}
