package com.into.websoso.feature.library.filter.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.domain.library.model.Genre
import com.into.websoso.domain.library.model.Genres

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun LibraryFilterBottomSheetGenre(
    genres: Genres,
    onGenreClick: (Genre) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Genre.entries.forEach { genre ->
            LibraryFilterTagChip(
                label = genre.label,
                isSelected = genres[genre],
                onClick = { onGenreClick(genre) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetGenrePreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetGenre(
            genres = Genres(),
            onGenreClick = {},
        )
    }
}
