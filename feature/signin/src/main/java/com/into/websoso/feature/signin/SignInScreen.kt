package com.into.websoso.feature.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.into.websoso.core.auth.AuthClient
import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.common.extensions.collectAsEventWithLifecycle
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.feature.signin.UiEffect.ScrollToPage
import com.into.websoso.feature.signin.UiEffect.ShowToast
import com.into.websoso.feature.signin.component.OnboardingDotsIndicator
import com.into.websoso.feature.signin.component.OnboardingHorizontalPager
import com.into.websoso.feature.signin.component.Onboarding_Images
import com.into.websoso.feature.signin.component.SignInButtons

@Composable
fun SignInRoute(
    authClient: (platform: AuthPlatform) -> AuthClient,
    signInViewModel: SignInViewModel = hiltViewModel(),
) {
    val latestEvent by rememberUpdatedState(signInViewModel.uiEvent)
    var isScroll by remember { mutableStateOf(false) }

    latestEvent.collectAsEventWithLifecycle { event ->
        when (event) {
            is ScrollToPage -> {
                isScroll = true
            }

            is ShowToast -> {
                // TODO: 실패 시 커스텀 스낵 바 구현
            }
        }
    }

    SignInScreen(
        isScroll = isScroll,
        onScrollChanged = { isScroll = false },
        onClick = { platform ->
            signInViewModel.signIn(platform = platform) {
                authClient(platform).signIn()
            }
        },
    )
}

@Composable
private fun SignInScreen(
    isScroll: Boolean,
    onScrollChanged: () -> Unit,
    onClick: (platform: AuthPlatform) -> Unit,
) {
    val pagerState = rememberPagerState { Onboarding_Images.size }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Gray50),
    ) {
        Spacer(modifier = Modifier.weight(weight = 1f))
        OnboardingHorizontalPager(
            pagerState = pagerState,
            isScroll = isScroll,
            onScrollChanged = onScrollChanged,
        )
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
        SignInScreen(
            isScroll = true,
            onScrollChanged = {},
            onClick = {},
        )
    }
}
