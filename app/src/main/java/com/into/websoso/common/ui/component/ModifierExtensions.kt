package com.into.websoso.common.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Modifier.clickableWithoutRipple(
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    return this.clickable(
        enabled = enabled,
        interactionSource = mutableInteractionSource,
        indication = null,
        onClick = onClick,
    )
}
