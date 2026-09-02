package com.into.websoso.feature.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.feature.collection.component.CollectionAppBar
import com.into.websoso.feature.collection.component.CollectionNovelSearchField
import com.into.websoso.feature.collection.component.CollectionNovelSelectionInfo

@Composable
internal fun CollectionNovelSearchScreen(
    addedNovelCount: Int,
    onNavigateBack: () -> Unit,
    onNavigateToLibraryNovelSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    val searchFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        searchFocusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding(),
    ) {
        CollectionAppBar(
            title = "작품 리스트",
            actionLabel = "완료",
            onNavigateBack = onNavigateBack,
            isActionEnabled = addedNovelCount > 0,
        )
        CollectionNovelSearchField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            onClearClick = { searchQuery = TextFieldValue() },
            focusRequester = searchFocusRequester,
            modifier = Modifier.padding(
                start = 20.dp,
                top = 10.dp,
                end = 20.dp,
            ),
        )
        CollectionNovelSelectionInfo(
            addedNovelCount = addedNovelCount,
            onAddFromLibraryClick = onNavigateToLibraryNovelSelection,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionNovelSearchScreenPreview() {
    WebsosoTheme {
        CollectionNovelSearchScreen(
            addedNovelCount = 0,
            onNavigateBack = {},
            onNavigateToLibraryNovelSelection = {},
        )
    }
}
