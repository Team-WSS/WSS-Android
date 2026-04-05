package com.into.websoso.feature.feed.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary50
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R
import com.into.websoso.feature.feed.model.MyFeedFilter
import com.into.websoso.feature.feed.model.NovelCategory

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MyFeedFilterModal(
    initialFilter: MyFeedFilter,
    onApplyFilterClick: (filter: MyFeedFilter) -> Unit,
    onFilterCloseClick: () -> Unit,
) {
    var tempGenres by remember { mutableStateOf(initialFilter.selectedGenres) }
    var tempIsVisible by remember { mutableStateOf(initialFilter.isVisible) }
    var tempIsUnVisible by remember { mutableStateOf(initialFilter.isUnVisible) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = White,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                ),
            )
            .navigationBarsPadding(),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
        ) {
            Text(
                text = "글 찾기 필터",
                style = WebsosoTheme.typography.body2,
                color = Gray200,
            )

            Box(
                modifier = Modifier
                    .debouncedClickable(onClick = onFilterCloseClick)
                    .padding(all = 20.dp),
            ) {
                Icon(
                    modifier = Modifier.size(size = 25.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_cancel_modal),
                    contentDescription = null,
                    tint = Gray300,
                )
            }
        }

        Row(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "카테고리",
                color = Black,
                style = WebsosoTheme.typography.title2,
                modifier = Modifier.padding(vertical = 10.dp),
            )
        }

        GenreChipGroup(
            selectedCategories = tempGenres,
            onCategoriesSelected = { tempGenres = it },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(modifier = Modifier.height(height = 32.dp))

        Row(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "공개여부",
                color = Black,
                style = WebsosoTheme.typography.title2,
                modifier = Modifier.padding(vertical = 10.dp),
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            VisibilityRow(
                text = "공개글",
                iconRes = R.drawable.ic_visible,
                isSelected = tempIsVisible == true,
                onClick = { tempIsVisible = if (tempIsVisible == true) null else true },
            )

            HorizontalDivider(color = Gray50)

            VisibilityRow(
                text = "비공개글",
                iconRes = R.drawable.ic_unvisible,
                isSelected = tempIsUnVisible == true,
                onClick = { tempIsUnVisible = if (tempIsUnVisible == true) null else true },
            )

            HorizontalDivider(color = Gray50)
        }

        Spacer(modifier = Modifier.height(height = 30.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 12.dp)
                .background(
                    color = Primary100,
                    shape = RoundedCornerShape(size = 14.dp),
                )
                .debouncedClickable {
                    onApplyFilterClick(MyFeedFilter(tempGenres, tempIsVisible, tempIsUnVisible))
                },
        ) {
            Text(
                text = "해당하는 글 보기",
                style = WebsosoTheme.typography.title2,
                color = White,
                modifier = Modifier.padding(vertical = 18.dp),
            )
        }
    }
}

@Composable
private fun VisibilityRow(
    text: String,
    @DrawableRes iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .debouncedClickable(onClick = onClick)
            .padding(vertical = 18.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(size = 18.dp),
                tint = if (isSelected) Black else Gray200,
            )
            Text(
                text = text,
                color = if (isSelected) Black else Gray200,
                style = WebsosoTheme.typography.body2,
            )
        }

        Image(
            painter = painterResource(
                id = if (isSelected) R.drawable.ic_novel_detail_check else R.drawable.ic_novel_unselected,
            ),
            contentDescription = null,
            modifier = Modifier.size(size = 24.dp),
        )
    }
}

@Composable
private fun GenreChipGroup(
    selectedCategories: Set<NovelCategory>,
    onCategoriesSelected: (Set<NovelCategory>) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalArrangement = Arrangement.spacedBy(space = 10.dp),
    ) {
        NovelCategory.entries.forEach { category ->
            val isSelected = selectedCategories.contains(category)

            GenreChip(
                text = category.koreanName,
                isSelected = isSelected,
                onChipClick = {
                    val next = if (isSelected) {
                        selectedCategories - category
                    } else {
                        selectedCategories + category
                    }
                    onCategoriesSelected(next)
                },
            )
        }
    }
}

@Composable
private fun GenreChip(
    text: String,
    isSelected: Boolean,
    onChipClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(size = 20.dp),
        border = BorderStroke(width = 1.dp, color = if (isSelected) Primary100 else Gray50),
        color = if (isSelected) Primary50 else Gray50,
        onClick = onChipClick,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = if (isSelected) Primary100 else Gray300,
            style = WebsosoTheme.typography.body2,
        )
    }
}

@Preview
@Composable
private fun MyFeedFilterModalPreview() {
    WebsosoTheme {
        MyFeedFilterModal(
            initialFilter = MyFeedFilter(),
            onApplyFilterClick = {},
            onFilterCloseClick = { },
        )
    }
}
