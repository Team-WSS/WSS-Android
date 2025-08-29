package com.into.websoso.feature.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.into.websoso.core.auth.AuthClient
import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.common.extensions.collectAsEventWithLifecycle
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.feature.signin.UiEffect.NavigateToHome
import com.into.websoso.feature.signin.UiEffect.NavigateToOnboarding
import com.into.websoso.feature.signin.UiEffect.ScrollToPage
import com.into.websoso.feature.signin.UiEffect.ShowToast
import com.into.websoso.feature.signin.component.OnboardingDotsIndicator
import com.into.websoso.feature.signin.component.OnboardingHorizontalPager
import com.into.websoso.feature.signin.component.Onboarding_Images
import com.into.websoso.feature.signin.component.SignInButtons

@Composable
fun SignInScreen(
    authClient: (platform: AuthPlatform) -> AuthClient?,
    websosoNavigator: NavigatorProvider,
    signInViewModel: SignInViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val latestEffect by rememberUpdatedState(signInViewModel.uiEffect)
    val pagerState = rememberPagerState { Onboarding_Images.size }

    latestEffect.collectAsEventWithLifecycle { effect ->
        when (effect) {
            ScrollToPage -> {
                pagerState.animateScrollToPage(
                    page = (pagerState.currentPage + 1) % pagerState.pageCount,
                )
            }

            ShowToast -> {}

            NavigateToHome -> websosoNavigator.navigateToMainActivity(context::startActivity)

            NavigateToOnboarding -> websosoNavigator.navigateToOnboardingActivity(context::startActivity)
        }
    }

    SignInScreen(
        pagerState = pagerState,
        onClick = { platform ->
            signInViewModel.signIn(
                platform = platform,
                signInToPlatform = authClient(platform)?.let { client -> client::signIn },
            )
        },
    )
}

@Composable
private fun SignInScreen(
    pagerState: PagerState,
    onClick: (platform: AuthPlatform) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Gray50)
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding(),
            ),
    ) {
        Spacer(modifier = Modifier.weight(weight = 1f))
        OnboardingHorizontalPager(pagerState = pagerState)
        Spacer(modifier = Modifier.weight(weight = 1f))
        OnboardingDotsIndicator(pagerState = pagerState)
        Spacer(modifier = Modifier.height(height = 32.dp))
        SignInButtons(onClick = onClick)
        Spacer(modifier = Modifier.height(height = 24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInScreenPreview() {
    WebsosoTheme {
        val pagerState = rememberPagerState { Onboarding_Images.size }

        SignInScreen(
            pagerState = pagerState,
            onClick = {},
        )
    }
}
