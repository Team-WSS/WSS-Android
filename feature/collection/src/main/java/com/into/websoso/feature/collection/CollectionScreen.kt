package com.into.websoso.feature.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.into.websoso.feature.collection.component.CollectionCreateButton
import com.into.websoso.feature.collection.component.CollectionTabRow
import com.into.websoso.feature.collection.model.CollectionTab

@Composable
fun CollectionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(CollectionTab.MY_COLLECTION) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding(),
    ) {
        CollectionAppBar(onNavigateBack = onNavigateBack)
        CollectionTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        )
        CollectionCreateButton(
            onClick = onNavigateToCreate,
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 16.dp,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionScreenPreview() {
    WebsosoTheme {
        CollectionScreen(
            onNavigateBack = {},
            onNavigateToCreate = {},
        )
    }
}
