package com.into.websoso.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity

object WebsosoTheme {
    val typography: WebsosoTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalWebsosoTypography.current
}

@Composable
fun WebsosoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val websosoTypography = remember { WebsosoTypography(density) }
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalWebsosoTypography provides websosoTypography) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

private val LocalWebsosoTypography = staticCompositionLocalOf<WebsosoTypography> {
    error("No WebsosoTypography provided")
}

private val DarkColorScheme = darkColorScheme(
    primary = Primary100,
    onPrimary = White,
    onTertiary = White,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary100,
    onPrimary = White,
    onTertiary = Black,
)
