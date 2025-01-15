package com.into.websoso.designsystem.theme

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
    secondary = Secondary100,
    background = Black,
    surface = Gray80,
    error = Warning,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary100,
    secondary = Secondary100,
    background = White,
    surface = Gray20,
    error = Warning,
)

private val LocalWebsosoTypography = staticCompositionLocalOf<WebsosoTypography> {
    error("No WebsosoTypography Provided")
}

object WebsosoTheme {
    val typography: WebsosoTypography
        @Composable get() = LocalWebsosoTypography.current
}

@Composable
fun ProvideWebsosoTypography(typography: WebsosoTypography, content: @Composable () -> Unit) {
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
