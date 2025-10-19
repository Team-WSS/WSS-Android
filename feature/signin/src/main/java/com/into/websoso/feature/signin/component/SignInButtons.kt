package com.into.websoso.feature.signin.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.common.extensions.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_login_kakao

@Composable
internal fun SignInButtons(
    onClick: (platform: AuthPlatform) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        KakaoSignInButton(onClick = onClick)
    }
}

@Composable
private fun KakaoSignInButton(
    onClick: (platform: AuthPlatform) -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        imageVector = ImageVector.vectorResource(id = ic_login_kakao),
        contentDescription = null,
        modifier = modifier.clickableWithoutRipple {
            onClick(AuthPlatform.KAKAO)
        },
    )
}

@Preview
@Composable
private fun SignInButtonsPreview() {
    WebsosoTheme {
        SignInButtons(onClick = {})
    }
}
