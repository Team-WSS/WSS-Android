package com.into.websoso.feature.collection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.string.collection_create_failed
import com.into.websoso.feature.collection.component.CollectionAppBar
import com.into.websoso.feature.collection.component.CollectionConfirmDialog
import com.into.websoso.feature.collection.component.CollectionDescriptionInput
import com.into.websoso.feature.collection.component.CollectionNameInput
import com.into.websoso.feature.collection.component.CollectionNovelSection
import com.into.websoso.feature.collection.component.CollectionPrivacySetting
import com.into.websoso.feature.collection.model.CollectionCreateUiState
import com.into.websoso.feature.collection.model.CollectionSelectedNovel
import kotlinx.coroutines.launch

@Composable
internal fun CollectionCreateScreen(
    selectedNovels: List<CollectionSelectedNovel>,
    representativeNovelId: Long?,
    createUiState: CollectionCreateUiState,
    onRepresentativeNovelClick: (Long) -> Unit,
    onCreateClick: (name: String, description: String, isPrivate: Boolean) -> Unit,
    onCreateResultConsumed: () -> Unit,
    onCreated: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToNovelSearch: () -> Unit,
    onDiscard: () -> Unit = onNavigateBack,
    modifier: Modifier = Modifier,
) {
    val initial = createUiState.initialCollection
    var isPrivate by rememberSaveable { mutableStateOf(initial?.isPublic == false) }
    var collectionName by rememberSaveable { mutableStateOf(initial?.name.orEmpty()) }
    var collectionDescription by rememberSaveable { mutableStateOf(initial?.description.orEmpty()) }
    var showDiscard by remember { mutableStateOf(false) }
    val requestBack = {
        if (!createUiState.isLoading) {
            if (initial != null) showDiscard = true else onNavigateBack()
        }
    }
    BackHandler { requestBack() }
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    val createFailedMessage = if (initial ==
        null
    ) {
        stringResource(collection_create_failed)
    } else {
        stringResource(com.into.websoso.core.resource.R.string.collection_update_failed)
    }

    LaunchedEffect(createUiState.createdCollectionId) {
        createUiState.createdCollectionId?.let { collectionId ->
            onCreateResultConsumed()
            onCreated(collectionId)
        }
    }
    LaunchedEffect(createUiState.isError) {
        if (createUiState.isError) {
            onCreateResultConsumed()
            snackbarScope.launch { snackbarHostState.showSnackbar(createFailedMessage) }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .statusBarsPadding(),
        ) {
            CollectionAppBar(
                actionLabel = stringResource(com.into.websoso.core.resource.R.string.collection_create_complete),
                onNavigateBack = requestBack,
                onActionClick = {
                    onCreateClick(collectionName, collectionDescription, isPrivate)
                },
                isActionEnabled =
                    collectionName.isNotBlank() &&
                        selectedNovels.isNotEmpty() &&
                        !createUiState.isLoading,
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                CollectionPrivacySetting(
                    isPrivate = isPrivate,
                    onPrivateChange = { if (!createUiState.isLoading) isPrivate = it },
                )
                CollectionNameInput(
                    value = collectionName,
                    onValueChange = { if (!createUiState.isLoading) collectionName = it },
                    modifier = Modifier.padding(
                        start = 20.dp,
                        top = 20.dp,
                        end = 20.dp,
                    ),
                )
                CollectionDescriptionInput(
                    value = collectionDescription,
                    onValueChange = { if (!createUiState.isLoading) collectionDescription = it },
                    modifier = Modifier.padding(
                        start = 20.dp,
                        top = 30.dp,
                        end = 20.dp,
                    ),
                )
                CollectionNovelSection(
                    selectedNovels = selectedNovels,
                    representativeNovelId = representativeNovelId,
                    onRepresentativeNovelClick = { if (!createUiState.isLoading) onRepresentativeNovelClick(it) },
                    onAddNovelClick = { if (!createUiState.isLoading) onNavigateToNovelSearch() },
                    modifier = Modifier.padding(
                        start = 20.dp,
                        top = 30.dp,
                        end = 20.dp,
                        bottom = 20.dp,
                    ),
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
        )
        if (createUiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        if (showDiscard) {
            CollectionConfirmDialog(false, { showDiscard = false }, onDiscard)
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
            createUiState = CollectionCreateUiState(),
            onRepresentativeNovelClick = {},
            onCreateClick = { _, _, _ -> },
            onCreateResultConsumed = {},
            onCreated = {},
            onNavigateBack = {},
            onNavigateToNovelSearch = {},
        )
    }
}
