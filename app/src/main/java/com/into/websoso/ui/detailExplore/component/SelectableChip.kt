package com.into.websoso.ui.detailExplore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary50
import com.into.websoso.core.designsystem.theme.Transparent
import com.into.websoso.core.designsystem.theme.WebsosoTheme

@Composable
fun SelectableTagChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SelectableChipBase(
        label = label,
        isSelected = isSelected,
        onClick = onClick,
        cornerRadius = 20.dp,
        horizontalPadding = 13.dp,
        verticalPadding = 7.dp,
        modifier = modifier,
    )
}

@Composable
fun SelectableStatusChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SelectableChipBase(
        label = label,
        isSelected = isSelected,
        onClick = onClick,
        cornerRadius = 8.dp,
        horizontalPadding = 0.dp,
        verticalPadding = 10.dp,
        modifier = modifier,
    )
}

@Composable
private fun SelectableChipBase(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    cornerRadius: Dp,
    horizontalPadding: Dp,
    verticalPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val background = if (isSelected) Primary50 else Gray50
    val borderColor = if (isSelected) Primary100 else Transparent
    val textColor = if (isSelected) Primary100 else Gray300

    Box(
        modifier = modifier
            .clip(shape)
            .clickableWithoutRipple(onClick = onClick)
            .background(color = background, shape = shape)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = WebsosoTheme.typography.body2,
            color = textColor,
        )
    }
}