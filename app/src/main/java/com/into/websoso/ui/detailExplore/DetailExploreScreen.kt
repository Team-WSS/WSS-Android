package com.into.websoso.ui.detailExplore

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.ui.detailExplore.component.DetailExploreAppBar
import com.into.websoso.ui.detailExplore.component.DetailExploreCtaButton
import com.into.websoso.ui.detailExplore.component.DetailExploreInfoTab
import com.into.websoso.ui.detailExplore.component.DetailExploreKeywordTab
import com.into.websoso.ui.detailExplore.model.SelectedFragmentTitle.INFO
import com.into.websoso.ui.detailExplore.model.SelectedFragmentTitle.KEYWORD

@Composable
fun DetailExploreScreen(
    viewModel: DetailExploreViewModel,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onKeywordInquireClick: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(INFO) }

    val isInfoChipSelected by viewModel.isInfoChipSelected.observeAsState(false)
    val isKeywordChipSelected by viewModel.isKeywordChipSelected.observeAsState(false)

    BackHandler(onBack = onBackClick)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(top = 10.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailExploreAppBar(
                selectedTab = selectedTab,
                isInfoChipActive = isInfoChipSelected,
                isKeywordChipActive = isKeywordChipSelected,
                onTabSelected = { selectedTab = it },
                onBackClick = onBackClick,
                onResetClick = {
                    when (selectedTab) {
                        INFO -> viewModel.updateSelectedInfoValueClear()
                        KEYWORD -> viewModel.updateSelectedKeywordValueClear()
                    }
                },
            )
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    INFO -> DetailExploreInfoTab(viewModel = viewModel)
                    KEYWORD -> DetailExploreKeywordTab(
                        viewModel = viewModel,
                        onKeywordInquireClick = onKeywordInquireClick,
                    )
                }
            }
            DetailExploreCtaButton(onClick = onSearchClick)
        }
    }
}
