package com.into.websoso.feature.library.filter.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.domain.library.model.Keywords

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun LibraryFilterBottomSheetKeyword(
    keywords: Keywords,
    onKeywordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (keywords.registered.isEmpty()) {
        Text(
            text = "등록한 키워드가 없습니다.",
            style = WebsosoTheme.typography.body2,
            color = Gray200,
            modifier = modifier.padding(top = 8.dp),
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "등록한 키워드 ${keywords.registered.size}개",
            style = WebsosoTheme.typography.body4,
            color = Gray300,
        )
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            keywords.registered.forEach { keyword ->
                LibraryFilterTagChip(
                    label = keyword.name,
                    isSelected = keywords.isSelected(keyword.name),
                    onClick = { onKeywordClick(keyword.name) },
                )
            }
        }
    }
}
