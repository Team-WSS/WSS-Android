package com.into.websoso.feature.library.filter.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.common.extensions.debouncedSelectable
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary50
import com.into.websoso.core.designsystem.theme.Transparent
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_novel_rating_keword_remove
import com.into.websoso.core.resource.R.string.library_filter_remove_selected_chip

@Composable
internal fun LibraryFilterTagChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LibraryFilterChipBase(
        label = label,
        isSelected = isSelected,
        onClick = onClick,
        cornerRadius = 20.dp,
        horizontalPadding = 9.5.dp,
        verticalPadding = 7.dp,
        modifier = modifier,
    )
}

@Composable
internal fun LibraryFilterStatusChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LibraryFilterChipBase(
        label = label,
        isSelected = isSelected,
        onClick = onClick,
        cornerRadius = 8.dp,
        horizontalPadding = 0.dp,
        verticalPadding = 12.dp,
        modifier = modifier,
    )
}

@Composable
private fun LibraryFilterChipBase(
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
            .debouncedSelectable(
                selected = isSelected,
                role = Role.Button,
                onClick = onClick,
            ).background(color = background, shape = shape)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = WebsosoTheme.typography.body5,
            color = textColor,
        )
    }
}

@Composable
internal fun LibraryFilterRemovableChip(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .border(width = 1.dp, color = Primary100, shape = shape)
            .background(color = White, shape = shape)
            .debouncedClickable(
                onClickLabel = stringResource(id = library_filter_remove_selected_chip),
                role = Role.Button,
                onClick = onRemove,
            ).padding(start = 13.dp, end = 11.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = WebsosoTheme.typography.body5,
            color = Primary100,
        )
        Image(
            painter = painterResource(id = ic_novel_rating_keword_remove),
            contentDescription = null,
            modifier = Modifier.size(10.dp),
        )
    }
}
