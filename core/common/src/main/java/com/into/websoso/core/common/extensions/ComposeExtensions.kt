package com.into.websoso.core.common.extensions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

@Composable
fun Modifier.clickableWithoutRipple(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
) = clickable(
    enabled = enabled,
    onClickLabel = onClickLabel,
    role = role,
    indication = null,
    interactionSource = remember { MutableInteractionSource() },
    onClick = onClick,
)
