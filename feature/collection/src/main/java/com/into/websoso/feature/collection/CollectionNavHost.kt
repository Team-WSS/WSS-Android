package com.into.websoso.feature.collection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.into.websoso.feature.collection.component.CollectionAppBar
import com.into.websoso.feature.collection.component.CollectionNetworkError

private const val COLLECTION_ROUTE = "collection"
private const val COLLECTION_DETAIL_ROUTE = "collection/detail/{collectionId}"
private const val COLLECTION_EDITOR_ROUTE = "collection/editor/{collectionId}"
private const val COLLECTION_SEARCH_ROUTE = "collection/editor/{collectionId}/search"
private const val COLLECTION_LIBRARY_ROUTE = "collection/editor/{collectionId}/library"

@Composable
fun CollectionNavHost(
    onNavigateBack: () -> Unit,
    onNovelClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    initialCollectionId: Long? = null,
    userId: Long? = null,
) {
    val navController = rememberNavController()
    val arguments = listOf(navArgument("collectionId") { type = NavType.LongType })
    val start = if (initialCollectionId != null) "collection/detail/$initialCollectionId" else COLLECTION_ROUTE
    val returnToMyList: () -> Unit = {
        navController.navigate(COLLECTION_ROUTE) {
            popUpTo(navController.graph.id) { inclusive = false }
            launchSingleTop = true
        }
        navController.currentBackStackEntry?.savedStateHandle?.set("showMyCollections", 1)
    }

    NavHost(navController, startDestination = start, modifier = modifier.navigationBarsPadding().imePadding()) {
        composable(COLLECTION_ROUTE) { entry ->
            val showMy by entry.savedStateHandle.getStateFlow("showMyCollections", 0).collectAsStateWithLifecycle()
            CollectionScreen(
                onNavigateBack = onNavigateBack,
                onNavigateToCreate = { navController.navigate("collection/editor/0") },
                onCollectionClick = { navController.navigate("collection/detail/$it") },
                userId = if (showMy > 0) null else userId,
                showMyCollections = showMy,
            )
        }
        composable(COLLECTION_DETAIL_ROUTE, arguments) {
            CollectionDetailScreen(
                onNavigateBack = { if (!navController.popBackStack()) onNavigateBack() },
                onEdit = { navController.navigate("collection/editor/$it") },
                onDeleted = returnToMyList,
                onNovelClick = onNovelClick,
            )
        }
        composable(COLLECTION_EDITOR_ROUTE, arguments) { entry ->
            val viewModel: CollectionNovelSearchViewModel = hiltViewModel(entry)
            val selected by viewModel.selectedNovels.collectAsStateWithLifecycle()
            val representative by viewModel.representativeNovelId.collectAsStateWithLifecycle()
            val state by viewModel.createUiState.collectAsStateWithLifecycle()
            val id = entry.arguments?.getLong("collectionId") ?: 0L
            if (state.isInitialLoading || state.isLoadError || (id != 0L && state.initialCollection == null)) {
                Box(Modifier.fillMaxSize()) {
                    CollectionAppBar(onNavigateBack = { navController.popBackStack() })
                    if (state.isLoadError) {
                        CollectionNetworkError(viewModel::loadCollection, Modifier.align(Alignment.Center))
                    } else {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
            } else {
                CollectionCreateScreen(
                    selectedNovels = selected,
                    representativeNovelId = representative,
                    createUiState = state,
                    onRepresentativeNovelClick = viewModel::updateRepresentativeNovel,
                    onCreateClick = viewModel::createCollection,
                    onCreateResultConsumed = viewModel::consumeCreateResult,
                    onCreated = { if (id == 0L) returnToMyList() else navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNovelSearch = { navController.navigate("collection/editor/$id/search") },
                    onDiscard = returnToMyList,
                )
            }
        }
        composable(COLLECTION_SEARCH_ROUTE, arguments) { entry ->
            val id = entry.arguments?.getLong("collectionId") ?: 0L
            val editorEntry = remember(entry) { navController.getBackStackEntry("collection/editor/$id") }
            val viewModel: CollectionNovelSearchViewModel = hiltViewModel(editorEntry)
            CollectionNovelSearchRoute(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLibraryNovelSelection = { navController.navigate("collection/editor/$id/library") },
            )
        }
        composable(COLLECTION_LIBRARY_ROUTE, arguments) { entry ->
            val id = entry.arguments?.getLong("collectionId") ?: 0L
            val editorEntry = remember(entry) { navController.getBackStackEntry("collection/editor/$id") }
            val viewModel: CollectionNovelSearchViewModel = hiltViewModel(editorEntry)
            val selected by viewModel.selectedNovels.collectAsStateWithLifecycle()
            CollectionLibraryNovelSelectionRoute(
                initialSelectedNovels = selected,
                onAddClick = {
                    viewModel.updateSelectedNovels(it)
                    navController.popBackStack("collection/editor/$id", inclusive = false)
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
