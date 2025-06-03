package com.into.websoso.feature.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LibraryScreen(libraryViewModel: LibraryViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "123123")
    }
}
