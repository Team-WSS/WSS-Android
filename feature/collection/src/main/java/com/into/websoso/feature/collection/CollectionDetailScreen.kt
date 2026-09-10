package com.into.websoso.feature.collection

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.core.designsystem.component.NetworkImage
import com.into.websoso.core.designsystem.component.S3Image
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Black60
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Gray80
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary20
import com.into.websoso.core.designsystem.theme.Primary30
import com.into.websoso.core.designsystem.theme.Transparent
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R
import com.into.websoso.domain.collection.model.CollectionDetail
import com.into.websoso.domain.collection.model.CollectionSortCriteria
import com.into.websoso.feature.collection.component.CollectionConfirmDialog
import com.into.websoso.feature.collection.component.CollectionNetworkError
import com.into.websoso.feature.collection.component.ellipsize
import com.into.websoso.feature.collection.model.CollectionShareContent
import kotlinx.coroutines.launch

@Composable
internal fun CollectionDetailScreen(
    onNavigateBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    onNovelClick: (Long) -> Unit,
    onShare: (CollectionShareContent) -> Unit,
    onShareBlocked: (collectionId: Long, novelCount: Int, novelsSize: Int, isPublic: Boolean) -> Unit,
    isSharing: Boolean,
    viewModel: CollectionDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val isCollapsed by remember { derivedStateOf { gridState.firstVisibleItemIndex > 0 } }
    var menuExpanded by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }
    var showSort by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    val shareDataOutdatedMessage = stringResource(R.string.collection_share_data_outdated)
    val errorMessage = state.error?.let { stringResource(it) }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }
    LaunchedEffect(state.isDeleted) { if (state.isDeleted) onDeleted() }
    LaunchedEffect(state.error) {
        if (state.error != null && state.collection != null && errorMessage != null) {
            viewModel.consumeError()
            snackbarScope.launch { snackbar.showSnackbar(errorMessage) }
        }
    }
    val collection = state.collection
    Box(Modifier.fillMaxSize().background(White).statusBarsPadding()) {
        if (collection == null) {
            Column {
                DetailAppBar("", true, false, onNavigateBack, {})
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (state.showInitialError) {
                        CollectionNetworkError(viewModel::refresh)
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        } else {
            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    state = gridState,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp),
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        CollectionDetailHeader(
                            collection = collection,
                            isBusy = state.isBusy || state.isLoading,
                            isSharing = isSharing,
                            onLike = viewModel::toggleLike,
                            onShare = {
                                val content = CollectionShareContent.from(collection)
                                if (content != null) {
                                    onShare(content)
                                } else {
                                    Log.w(
                                        "CollectionDetailScreen",
                                        "collection ${collection.id} failed to build share content: " +
                                            "novelCount=${collection.novelCount}, novels=${collection.novels.size}, " +
                                            "representativeNovelId=${collection.representativeNovelId}",
                                    )
                                    onShareBlocked(
                                        collection.id,
                                        collection.novelCount,
                                        collection.novels.size,
                                        collection.isPublic,
                                    )
                                    snackbarScope.launch {
                                        snackbar.showSnackbar(shareDataOutdatedMessage)
                                    }
                                }
                            },
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                stringResource(
                                    R.string.collection_create_added_novel_count,
                                    collection.novelCount,
                                ),
                                color = Gray200,
                                style = WebsosoTheme.typography.body3,
                            )
                            Row(
                                Modifier.clickable { showSort = true }.padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    painterResource(R.drawable.ic_library_sort),
                                    null,
                                    Modifier.size(16.dp),
                                    tint = Gray200,
                                )
                                Text(
                                    state.sort.label,
                                    color = Gray300,
                                    style = WebsosoTheme.typography.body3,
                                )
                            }
                        }
                    }
                    items(
                        collection.novels.chunked(3),
                        key = { it.first().id },
                        span = { GridItemSpan(maxLineSpan) },
                    ) { row ->
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            row.forEach { novel ->
                                Column(
                                    Modifier
                                        .weight(1f)
                                        .clickable(role = Role.Button) { onNovelClick(novel.id) },
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    NetworkImage(
                                        imageUrl = novel.imageUrl,
                                        contentDescription = novel.title,
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.BottomCenter,
                                        placeholder = painterResource(R.drawable.img_collection_empty_cover),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                    )
                                    Text(
                                        novel.title,
                                        color = Black,
                                        style = WebsosoTheme.typography.body4,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        novel.author,
                                        color = Gray200,
                                        style = WebsosoTheme.typography.label2,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
            DetailAppBar(
                title = if (isCollapsed) collection.name.ellipsize(18) else "",
                isCollapsed = isCollapsed,
                isMine = collection.isMine,
                onNavigateBack = onNavigateBack,
                onMenuClick = { menuExpanded = true },
            )
            if (menuExpanded && collection.isMine) {
                Box(Modifier.align(Alignment.TopEnd).padding(end = 15.dp)) {
                    CollectionDetailMenu(
                        enabled = !state.isBusy && !state.isLoading,
                        onDismiss = { menuExpanded = false },
                        onEdit = {
                            menuExpanded = false
                            onEdit(collection.id)
                        },
                        onDelete = {
                            menuExpanded = false
                            showDelete = true
                        },
                    )
                }
            }
            if (showDelete) {
                CollectionConfirmDialog(true, { showDelete = false }) {
                    showDelete = false
                    viewModel.delete()
                }
            }
            if (showSort) {
                CollectionSortSheet(state.sort, { showSort = false }, viewModel::sort)
            }
            if (state.isBusy) CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun CollectionDetailMenu(
    enabled: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Popup(
        alignment = Alignment.TopEnd,
        offset = IntOffset(0, with(LocalDensity.current) { 54.dp.roundToPx() }),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        val shape = RoundedCornerShape(14.dp)
        Column(
            Modifier
                .width(122.dp)
                .dropShadow(
                    shape,
                    Shadow(
                        radius = 7.5.dp,
                        color = Black60.copy(alpha = 0.11f),
                        offset = DpOffset(0.dp, 2.dp),
                    ),
                ).clip(shape)
                .background(White),
        ) {
            Text(
                text = stringResource(R.string.collection_edit),
                color = Black,
                style = WebsosoTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled, role = Role.Button, onClick = onEdit)
                    .padding(15.dp),
            )
            HorizontalDivider(thickness = 0.7.dp, color = Gray50)
            Text(
                text = stringResource(R.string.collection_delete),
                color = Black,
                style = WebsosoTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled, role = Role.Button, onClick = onDelete)
                    .padding(horizontal = 15.dp, vertical = 14.dp),
            )
        }
    }
}

@Composable
private fun DetailAppBar(
    title: String,
    isCollapsed: Boolean,
    isMine: Boolean,
    onNavigateBack: () -> Unit,
    onMenuClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(
                if (isCollapsed ||
                    title.isNotEmpty()
                ) {
                    White
                } else {
                    Transparent
                },
            ).padding(start = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onNavigateBack, modifier = Modifier.size(44.dp)) {
            Icon(
                painterResource(R.drawable.ic_navigate_left),
                stringResource(R.string.collection_back),
                tint = if (isCollapsed) Gray300 else White,
            )
        }
        Text(
            title,
            Modifier.weight(1f),
            color = Black,
            style = WebsosoTheme.typography.title2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (isMine) {
            IconButton(onMenuClick, modifier = Modifier.width(60.dp).height(44.dp)) {
                Icon(
                    painterResource(R.drawable.ic_three_dots),
                    stringResource(R.string.collection_menu),
                    Modifier.size(20.dp).rotate(90f),
                    tint = if (isCollapsed) Gray300 else White,
                )
            }
        } else {
            Spacer(Modifier.width(60.dp))
        }
    }
}

@Composable
private fun CollectionDetailHeader(
    collection: CollectionDetail,
    isBusy: Boolean,
    isSharing: Boolean,
    onLike: () -> Unit,
    onShare: () -> Unit,
) {
    Box(Modifier.fillMaxWidth().heightIn(min = 328.dp)) {
        NetworkImage(
            imageUrl = collection.novels
                .firstOrNull { it.id == collection.representativeNovelId }
                ?.imageUrl
                .orEmpty(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter,
            modifier = Modifier.matchParentSize(),
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    listOf(Black60.copy(alpha = 0.51f), Black60.copy(alpha = 0.85f)),
                ),
            ),
        )
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 156.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                S3Image(
                    imageUrl = collection.owner.avatarImageUrl,
                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)),
                )
                Text(
                    collection.owner.nickname,
                    color = White,
                    style = WebsosoTheme.typography.body4,
                )
            }
            Text(collection.name, color = White, style = WebsosoTheme.typography.headline1)
            collection.description?.takeIf(String::isNotBlank)?.let {
                Text(it, color = White, style = WebsosoTheme.typography.body3)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 6.dp),
            ) {
                Row(
                    Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(if (collection.isLiked) Primary30 else White)
                        .border(1.dp, Primary100, RoundedCornerShape(15.dp))
                        .toggleable(
                            value = collection.isLiked,
                            enabled = !isBusy,
                            role = Role.Checkbox,
                        ) { onLike() },
                    horizontalArrangement = Arrangement.spacedBy(
                        9.dp,
                        Alignment.CenterHorizontally,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painterResource(if (collection.isLiked) R.drawable.ic_thumb_up_on else R.drawable.ic_thumb_up),
                        null,
                        Modifier.size(20.dp),
                        tint = Primary100,
                    )
                    Text(
                        stringResource(R.string.collection_like_count, collection.likeCount),
                        color = Primary100,
                        style = WebsosoTheme.typography.body4,
                    )
                }
                Row(
                    Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(if (collection.isPublic) Primary100 else Gray80)
                        .clickable(
                            enabled = collection.isPublic && !isBusy && !isSharing,
                            role = Role.Button,
                            onClick = onShare,
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        9.dp,
                        Alignment.CenterHorizontally,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isSharing) {
                        CircularProgressIndicator(
                            Modifier.size(16.dp),
                            color = White,
                            strokeWidth = 2.dp,
                        )
                    }
                    if (!collection.isPublic) {
                        Icon(
                            painterResource(R.drawable.ic_lock),
                            null,
                            Modifier.size(20.dp),
                            tint = Gray200,
                        )
                    }
                    Text(
                        stringResource(
                            when {
                                !collection.isPublic -> R.string.collection_create_private
                                isSharing -> R.string.collection_share_preparing
                                else -> R.string.collection_share
                            },
                        ),
                        color = if (collection.isPublic) White else Gray200,
                        style = WebsosoTheme.typography.body4,
                    )
                }
            }
        }
    }
}

private val CollectionSortCriteria.label: String
    @Composable get() = stringResource(
        if (this ==
            CollectionSortCriteria.RECENT
        ) {
            R.string.collection_sort_recent
        } else {
            R.string.collection_sort_old
        },
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionSortSheet(
    selected: CollectionSortCriteria,
    onDismiss: () -> Unit,
    onSelected: (CollectionSortCriteria) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            CollectionSortCriteria.entries.forEach { criteria ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (criteria == selected) Primary20 else White)
                        .clickable {
                            onSelected(criteria)
                            onDismiss()
                        }.padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        Alignment.CenterHorizontally,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(20.dp)) {
                        if (criteria == selected) {
                            Icon(
                                painterResource(R.drawable.ic_library_sort_check),
                                null,
                                tint = Primary100,
                            )
                        }
                    }
                    Text(
                        criteria.label,
                        color = if (criteria == selected) Black else Gray200,
                        style = WebsosoTheme.typography.body2,
                    )
                    Spacer(Modifier.size(20.dp))
                }
            }
        }
    }
}
