package com.into.websoso.feature.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val COLLECTION_ROUTE = "collection"
private const val COLLECTION_CREATE_ROUTE = "collection/create"
private const val COLLECTION_NOVEL_SEARCH_ROUTE = "collection/create/novel-search"
private const val COLLECTION_LIBRARY_NOVEL_SELECTION_ROUTE =
    "collection/create/novel-search/library"

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
        composable(route = COLLECTION_CREATE_ROUTE) { backStackEntry ->
            val novelSearchViewModel: CollectionNovelSearchViewModel = hiltViewModel(backStackEntry)
            val selectedNovels by novelSearchViewModel.selectedNovels.collectAsStateWithLifecycle()
            val representativeNovelId by
                novelSearchViewModel.representativeNovelId.collectAsStateWithLifecycle()

            CollectionCreateScreen(
                selectedNovels = selectedNovels,
                representativeNovelId = representativeNovelId,
                onRepresentativeNovelClick = novelSearchViewModel::updateRepresentativeNovel,
                onNavigateBack = navController::popBackStack,
                onNavigateToNovelSearch = {
                    navController.navigate(COLLECTION_NOVEL_SEARCH_ROUTE)
                },
            )
        }
        composable(route = COLLECTION_NOVEL_SEARCH_ROUTE) { backStackEntry ->
            val createBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry(COLLECTION_CREATE_ROUTE)
            }
            val novelSearchViewModel: CollectionNovelSearchViewModel = hiltViewModel(createBackStackEntry)

            CollectionNovelSearchRoute(
                viewModel = novelSearchViewModel,
                onNavigateBack = navController::popBackStack,
                onNavigateToLibraryNovelSelection = {
                    navController.navigate(COLLECTION_LIBRARY_NOVEL_SELECTION_ROUTE)
                },
            )
        }
        composable(route = COLLECTION_LIBRARY_NOVEL_SELECTION_ROUTE) { backStackEntry ->
            val createBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry(COLLECTION_CREATE_ROUTE)
            }
            val novelSearchViewModel: CollectionNovelSearchViewModel = hiltViewModel(createBackStackEntry)

            CollectionLibraryNovelSelectionRoute(
                initialSelectedNovels = novelSearchViewModel.selectedNovels.value,
                onAddClick = { selectedNovels ->
                    novelSearchViewModel.updateSelectedNovels(selectedNovels)
                    navController.popBackStack()
                },
                onNavigateBack = navController::popBackStack,
            )
        }
    }
}
