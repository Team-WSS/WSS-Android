package com.into.websoso.feature.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R
import com.into.websoso.feature.collection.component.HomeCollectionCard

/** 홈의 공개 컬렉션 섹션. 보여줄 컬렉션이 없으면 아무것도 그리지 않는다. */
@Composable
fun HomeCollectionSection(
    onListClick: () -> Unit,
    onCollectionClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeCollectionViewModel = hiltViewModel()
    val collections by viewModel.collections.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }
    if (collections.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth().background(White).padding(top = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .debouncedClickable(role = Role.Button, onClick = onListClick)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.collection_public_title),
                color = Black,
                style = WebsosoTheme.typography.headline1,
                modifier = Modifier.weight(1f),
            )
            Icon(painterResource(R.drawable.ic_navigate_right), null, tint = Gray300, modifier = Modifier.size(24.dp))
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(collections, key = { it.id }) { collection ->
                HomeCollectionCard(collection, onClick = { onCollectionClick(collection.id) })
            }
        }
    }
}
