package com.into.websoso.feature.collection.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray70New
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.feature.collection.model.CollectionTab

@Composable
internal fun CollectionTabRow(
    selectedTab: CollectionTab,
    onTabSelected: (CollectionTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(47.dp)
            .background(White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
        ) {
            CollectionTab.entries.forEach { tab ->
                CollectionTabItem(
                    title = tab.title,
                    isSelected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(Gray70New),
        )
    }
}

@Composable
private fun CollectionTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(46.dp)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.Tab,
            ).padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            color = if (isSelected) Black else Gray100,
            style = WebsosoTheme.typography.title2,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Black),
            )
        }
    }
}

private val CollectionTab.title: String
    get() = when (this) {
        CollectionTab.MY_COLLECTION -> "내 컬렉션"
        CollectionTab.LIKED_COLLECTION -> "좋아요한 컬렉션"
    }

@Preview(showBackground = true)
@Composable
private fun CollectionTabRowPreview() {
    WebsosoTheme {
        CollectionTabRow(
            selectedTab = CollectionTab.MY_COLLECTION,
            onTabSelected = {},
        )
    }
}
