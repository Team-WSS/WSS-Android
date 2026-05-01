package com.into.websoso.ui.detailExplore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.ui.model.CategoriesModel.CategoryModel
import com.into.websoso.core.common.ui.model.CategoriesModel.CategoryModel.KeywordModel
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.core.designsystem.component.NetworkImage
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_common_search
import com.into.websoso.core.resource.R.drawable.ic_common_search_clear
import com.into.websoso.core.resource.R.drawable.ic_detail_explore_keyword_not_exist_result
import com.into.websoso.core.resource.R.drawable.ic_novel_rating_keword_remove
import com.into.websoso.core.resource.R.drawable.ic_novel_rating_keyword_toggle_selected
import com.into.websoso.core.resource.R.string.detail_explore_keyword_inquire_keyword
import com.into.websoso.core.resource.R.string.detail_explore_keyword_not_exist_result
import com.into.websoso.core.resource.R.string.detail_explore_keyword_search_result
import com.into.websoso.core.resource.R.string.detail_explore_search_hint
import com.into.websoso.ui.detailExplore.DetailExploreViewModel
import com.into.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordUiState
import androidx.compose.ui.platform.LocalContext

@Composable
fun DetailExploreKeywordTab(
    viewModel: DetailExploreViewModel,
    onKeywordInquireClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.observeAsState(DetailExploreKeywordUiState())
    var searchValue by remember { mutableStateOf(TextFieldValue("")) }

    val selectedKeywords = remember(uiState.categories) {
        uiState.categories.flatMap { it.keywords }.filter { it.isSelected }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        KeywordSearchField(
            value = searchValue,
            onValueChange = { value ->
                searchValue = value
                if (value.text.isEmpty()) {
                    viewModel.initSearchKeyword()
                } else {
                    viewModel.updateIsSearchKeywordProceeding(true)
                }
            },
            onSearch = {
                if (searchValue.text.isEmpty()) {
                    viewModel.initSearchKeyword()
                } else {
                    viewModel.updateKeyword(searchValue.text)
                }
            },
            onClear = {
                searchValue = TextFieldValue("")
                viewModel.initSearchKeyword()
            },
        )

        if (selectedKeywords.isNotEmpty()) {
            SelectedKeywordsRow(
                keywords = selectedKeywords,
                onRemove = viewModel::updateClickedChipState,
            )
        }

        when {
            uiState.isSearchKeywordProceeding && uiState.isSearchResultKeywordsEmpty -> {
                KeywordEmptyResult(onInquireClick = onKeywordInquireClick)
            }

            uiState.isSearchKeywordProceeding -> {
                Divider()
                SearchResultList(
                    keywords = uiState.searchResultKeywords,
                    isInitial = uiState.isInitialSearchKeyword,
                    onKeywordClick = viewModel::updateClickedChipState,
                )
            }

            else -> {
                CategoryList(
                    categories = uiState.categories,
                    onKeywordClick = viewModel::updateClickedChipState,
                )
            }
        }
    }
}

@Composable
private fun KeywordSearchField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Gray50)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                    cursorBrush = SolidColor(Primary100),
                    textStyle = WebsosoTheme.typography.body4.copy(color = Black),
                )
                if (value.text.isEmpty()) {
                    Text(
                        text = stringResource(detail_explore_search_hint),
                        style = WebsosoTheme.typography.body4,
                        color = Gray200,
                    )
                }
            }
            if (value.text.isNotEmpty()) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = ic_common_search_clear),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .clickableWithoutRipple(onClick = onClear),
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            androidx.compose.foundation.Image(
                painter = painterResource(id = ic_common_search),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickableWithoutRipple(onClick = onSearch),
            )
        }
    }
}

@Composable
private fun SelectedKeywordsRow(
    keywords: List<KeywordModel>,
    onRemove: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        keywords.forEach { keyword ->
            SelectedKeywordChip(
                label = keyword.keywordName,
                onRemove = { onRemove(keyword.keywordId) },
            )
        }
    }
}

@Composable
private fun SelectedKeywordChip(
    label: String,
    onRemove: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .border(width = 1.dp, color = Primary100, shape = shape)
            .background(color = White, shape = shape)
            .clickableWithoutRipple(onClick = onRemove)
            .padding(start = 13.dp, end = 11.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = WebsosoTheme.typography.body2,
            color = Primary100,
        )
        androidx.compose.foundation.Image(
            painter = painterResource(id = ic_novel_rating_keword_remove),
            contentDescription = null,
            modifier = Modifier.size(10.dp),
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Gray50),
    )
}

@Composable
private fun CategoryList(
    categories: List<CategoryModel>,
    onKeywordClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray50),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = categories, key = { it.categoryName }) { category ->
            CategoryCard(category = category, onKeywordClick = onKeywordClick)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryCard(
    category: CategoryModel,
    onKeywordClick: (Int) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imageUrl = remember(category.categoryImage) {
        context.getS3ImageUrl(category.categoryImage)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(White),
    ) {
        Row(
            modifier = Modifier
                .padding(start = 20.dp, top = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NetworkImage(
                imageUrl = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(10.dp)),
            )
            Text(
                text = category.categoryName,
                style = WebsosoTheme.typography.title2,
                color = Gray300,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        FlowRow(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .heightIn(max = if (isExpanded) Int.MAX_VALUE.dp else 92.dp)
                .clip(RoundedCornerShape(0.dp)),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            category.keywords.forEach { keyword ->
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    SelectableTagChip(
                        label = keyword.keywordName,
                        isSelected = keyword.isSelected,
                        onClick = { onKeywordClick(keyword.keywordId) },
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Gray50),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickableWithoutRipple { isExpanded = !isExpanded },
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = ic_novel_rating_keyword_toggle_selected),
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(if (isExpanded) 0f else 180f),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchResultList(
    keywords: List<KeywordModel>,
    isInitial: Boolean,
    onKeywordClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        if (!isInitial) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = stringResource(detail_explore_keyword_search_result),
                style = WebsosoTheme.typography.title3,
                color = Gray300,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            keywords.forEach { keyword ->
                SelectableTagChip(
                    label = keyword.keywordName,
                    isSelected = keyword.isSelected,
                    onClick = { onKeywordClick(keyword.keywordId) },
                )
            }
        }
    }
}

@Composable
private fun KeywordEmptyResult(onInquireClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = ic_detail_explore_keyword_not_exist_result),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(detail_explore_keyword_not_exist_result),
            style = WebsosoTheme.typography.body1,
            color = Gray200,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(36.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(Gray50)
                .clickableWithoutRipple(onClick = onInquireClick)
                .padding(horizontal = 26.dp, vertical = 20.dp),
        ) {
            Text(
                text = stringResource(detail_explore_keyword_inquire_keyword),
                style = WebsosoTheme.typography.title2,
                color = Primary100,
            )
        }
    }
}