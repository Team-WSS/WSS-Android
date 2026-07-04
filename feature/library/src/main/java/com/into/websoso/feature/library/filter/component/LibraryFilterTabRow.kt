package com.into.websoso.feature.library.filter.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.extensions.debouncedSelectable
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Transparent
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.feature.library.filter.LibraryFilterTab

@Composable
internal fun LibraryFilterTabRow(
    selectedTab: LibraryFilterTab,
    activeTabs: Set<LibraryFilterTab>,
    onTabSelected: (LibraryFilterTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        LibraryFilterTab.entries.forEach { tab ->
            LibraryFilterTabItem(
                tab = tab,
                isSelected = tab == selectedTab,
                isActive = tab in activeTabs,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

@Composable
private fun LibraryFilterTabItem(
    tab: LibraryFilterTab,
    isSelected: Boolean,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .debouncedSelectable(selected = isSelected, onClick = onClick),
    ) {
        Box {
            Text(
                text = tab.label,
                style = WebsosoTheme.typography.title2.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                ),
                color = if (isSelected) Black else Gray200,
                modifier = Modifier.padding(vertical = 10.dp),
            )
            if (isActive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 5.dp, y = 9.dp)
                        .size(3.dp)
                        .background(color = Primary100, shape = CircleShape),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(color = if (isSelected) Black else Transparent),
        )
    }
}
