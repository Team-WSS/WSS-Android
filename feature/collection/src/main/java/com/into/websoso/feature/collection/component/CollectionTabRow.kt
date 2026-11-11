package com.into.websoso.feature.collection.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
    SecondaryTabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier.fillMaxWidth(),
        containerColor = White,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(
                    selectedTabIndex = selectedTab.ordinal,
                    matchContentSize = false,
                ),
                height = 2.dp,
                color = Black,
            )
        },
        divider = {
            HorizontalDivider(thickness = 1.dp, color = Gray70New)
        },
    ) {
        CollectionTab.entries.forEach { tab ->
            CollectionTabItem(
                title = stringResource(tab.titleRes),
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
            )
        }
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
            .fillMaxWidth()
            .height(47.dp)
            .selectable(
                selected = isSelected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
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
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionTabRowPreview() {
    WebsosoTheme {
        Column {
            CollectionTab.entries.forEach { selectedTab ->
                CollectionTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = {},
                )
            }
        }
    }
}
