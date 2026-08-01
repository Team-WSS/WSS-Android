package com.into.websoso.feature.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val COLLECTION_ROUTE = "collection"
private const val COLLECTION_CREATE_ROUTE = "collection/create"
private const val COLLECTION_NOVEL_SEARCH_ROUTE = "collection/create/novel-search"
private const val COLLECTION_LIBRARY_NOVEL_SELECTION_ROUTE =
    "collection/create/novel-search/library"
private const val SELECTED_NOVEL_IDS_KEY = "selectedNovelIds"

@Composable
fun CollectionNavHost(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = COLLECTION_ROUTE,
        modifier = modifier,
    ) {
        composable(route = COLLECTION_ROUTE) {
            CollectionScreen(
                onNavigateBack = onNavigateBack,
                onNavigateToCreate = {
                    navController.navigate(COLLECTION_CREATE_ROUTE)
                },
            )
        }
        composable(route = COLLECTION_CREATE_ROUTE) {
            CollectionCreateScreen(
                onNavigateBack = navController::popBackStack,
                onNavigateToNovelSearch = {
                    navController.navigate(COLLECTION_NOVEL_SEARCH_ROUTE)
                },
            )
        }
        composable(route = COLLECTION_NOVEL_SEARCH_ROUTE) { backStackEntry ->
            val selectedNovelIds by backStackEntry.savedStateHandle
                .getStateFlow(SELECTED_NOVEL_IDS_KEY, longArrayOf())
                .collectAsStateWithLifecycle()

            CollectionNovelSearchScreen(
                addedNovelCount = selectedNovelIds.size,
                onNavigateBack = navController::popBackStack,
                onNavigateToLibraryNovelSelection = {
                    navController.navigate(COLLECTION_LIBRARY_NOVEL_SELECTION_ROUTE)
                },
            )
        }
        composable(route = COLLECTION_LIBRARY_NOVEL_SELECTION_ROUTE) {
            val initialSelectedNovelIds = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<LongArray>(SELECTED_NOVEL_IDS_KEY)
                ?.toSet()
                .orEmpty()

            CollectionLibraryNovelSelectionRoute(
                initialSelectedNovelIds = initialSelectedNovelIds,
                onAddClick = { selectedNovelIds ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(SELECTED_NOVEL_IDS_KEY, selectedNovelIds.toLongArray())
                    navController.popBackStack()
                },
                onNavigateBack = navController::popBackStack,
            )
        }
    }
}
