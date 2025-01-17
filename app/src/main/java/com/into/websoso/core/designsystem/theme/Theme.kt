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
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary100,
    onPrimary = White,
    primaryContainer = Primary200,
    onPrimaryContainer = Gray20,
    secondary = Secondary100,
    onSecondary = White,
    onSecondaryContainer = Gray50,
    tertiary = Gray300,
    onTertiary = White,
    background = Black,
    onBackground = Gray20,
    onSurface = Gray200,
    onError = White,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary100,
    onPrimary = White,
    primaryContainer = Primary50,
    onPrimaryContainer = Gray20,
    secondary = Secondary100,
    onSecondary = White,
    onSecondaryContainer = Gray50,
    tertiary = Gray300,
    onTertiary = Black,
    background = White,
    onBackground = Gray300,
    surface = Gray20,
    onSurface = Gray70,
    onError = White,
)

private val LocalWebsosoTypography = staticCompositionLocalOf<WebsosoTypography> {
    error("No WebsosoTypography Provided")
}

object WebsosoTheme {
    val typography: WebsosoTypography
        @Composable get() = LocalWebsosoTypography.current
}

@Composable
fun ProvideWebsosoTypography(
    typography: WebsosoTypography,
    content: @Composable () -> Unit,
) {
    val provideTypography = remember { typography.copy() }
    provideTypography.update(typography)
    CompositionLocalProvider(
        LocalWebsosoTypography provides provideTypography,
        content = content,
    )
}

@Composable
fun WebsosoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    val typography = websosoTypography()

    ProvideWebsosoTypography(typography) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}
