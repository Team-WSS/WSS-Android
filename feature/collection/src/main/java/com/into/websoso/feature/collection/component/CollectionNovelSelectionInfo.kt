package com.into.websoso.feature.collection.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.string.collection_create_add_from_library
import com.into.websoso.core.resource.R.string.collection_create_added_novel_count
import com.into.websoso.core.resource.R.string.collection_create_added_novels

@Composable
internal fun CollectionNovelSelectionInfo(
    addedNovelCount: Int,
    onAddFromLibraryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(collection_create_added_novels),
                color = Gray200,
                style = WebsosoTheme.typography.body4,
            )
            Text(
                text = stringResource(collection_create_added_novel_count, addedNovelCount),
                color = Primary100,
                style = WebsosoTheme.typography.body4,
            )
        }
        Text(
            text = stringResource(collection_create_add_from_library),
            color = Gray200,
            style = WebsosoTheme.typography.body4.copy(
                textDecoration = TextDecoration.Underline,
            ),
            modifier = Modifier.clickable(
                onClick = onAddFromLibraryClick,
                role = Role.Button,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionNovelSelectionInfoPreview() {
    WebsosoTheme {
        CollectionNovelSelectionInfo(
            addedNovelCount = 0,
            onAddFromLibraryClick = {},
        )
    }
}
