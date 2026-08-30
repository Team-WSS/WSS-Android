package com.into.websoso.feature.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.feature.collection.component.CollectionAppBar
import com.into.websoso.feature.collection.component.CollectionDescriptionInput
import com.into.websoso.feature.collection.component.CollectionNameInput
import com.into.websoso.feature.collection.component.CollectionNovelSection
import com.into.websoso.feature.collection.component.CollectionPrivacySetting
import com.into.websoso.feature.collection.model.CollectionSelectedNovel

@Composable
internal fun CollectionCreateScreen(
    selectedNovels: List<CollectionSelectedNovel>,
    representativeNovelId: Long?,
    onRepresentativeNovelClick: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToNovelSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPrivate by rememberSaveable { mutableStateOf(false) }
    var collectionName by rememberSaveable { mutableStateOf("") }
    var collectionDescription by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding(),
    ) {
        CollectionAppBar(
            actionLabel = "완료",
            onNavigateBack = onNavigateBack,
            isActionEnabled = collectionName.isNotBlank() && selectedNovels.isNotEmpty(),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            CollectionPrivacySetting(
                isPrivate = isPrivate,
                onPrivateChange = { isPrivate = it },
            )
            CollectionNameInput(
                value = collectionName,
                onValueChange = { collectionName = it },
                modifier = Modifier.padding(
                    start = 20.dp,
                    top = 20.dp,
                    end = 20.dp,
                ),
            )
            CollectionDescriptionInput(
                value = collectionDescription,
                onValueChange = { collectionDescription = it },
                modifier = Modifier.padding(
                    start = 20.dp,
                    top = 30.dp,
                    end = 20.dp,
                ),
            )
            CollectionNovelSection(
                selectedNovels = selectedNovels,
                representativeNovelId = representativeNovelId,
                onRepresentativeNovelClick = onRepresentativeNovelClick,
                onAddNovelClick = onNavigateToNovelSearch,
                modifier = Modifier.padding(
                    start = 20.dp,
                    top = 30.dp,
                    end = 20.dp,
                    bottom = 20.dp,
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionCreateScreenPreview() {
    WebsosoTheme {
        CollectionCreateScreen(
            selectedNovels = emptyList(),
            representativeNovelId = null,
            onRepresentativeNovelClick = {},
            onNavigateBack = {},
            onNavigateToNovelSearch = {},
        )
    }
}
