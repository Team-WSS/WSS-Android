package com.into.websoso.feature.collection

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val COLLECTION_ROUTE = "collection"
private const val COLLECTION_CREATE_ROUTE = "collection/create"

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
            CollectionCreateScreen()
        }
    }
}
