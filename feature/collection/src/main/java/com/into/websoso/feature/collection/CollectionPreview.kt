package com.into.websoso.feature.collection

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.core.designsystem.component.NetworkImage
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray80
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R
import com.into.websoso.feature.collection.component.CollectionEntry

@Composable
fun CollectionPreview(
    onListClick: () -> Unit,
    onCollectionClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    userId: Long? = null,
) {
    val viewModel: CollectionPreviewViewModel = hiltViewModel(key = "collection-preview-${userId ?: "me"}")
    val page by viewModel.page.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isError by viewModel.isError.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh(userId) }
    Column(modifier.fillMaxWidth().background(White)) {
        val current = page
        if (current != null) {
            CollectionEntry(
                collectionCount = current.totalCount,
                onClick = {
                    if (userId != null && current.totalCount == 0) {
                        Toast.makeText(context, R.string.collection_empty, Toast.LENGTH_SHORT).show()
                    } else {
                        onListClick()
                    }
                },
            )
            if (current.collections.isNotEmpty()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                ) {
                    current.collections.take(3).forEach { collection ->
                        Column(
                            Modifier.width(88.dp).clickable { onCollectionClick(collection.id) },
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Box(Modifier.size(88.dp, 108.397.dp)) {
                                Box(
                                    Modifier
                                        .offset(x = 14.dp)
                                        .size(73.907.dp, 108.397.dp)
                                        .clip(RoundedCornerShape(6.569.dp))
                                        .background(Gray200),
                                )
                                Box(
                                    Modifier
                                        .offset(x = 7.dp)
                                        .size(73.907.dp, 108.397.dp)
                                        .clip(RoundedCornerShape(6.569.dp))
                                        .background(Gray80),
                                )
                                NetworkImage(
                                    imageUrl = collection.representativeNovel.imageUrl,
                                    contentDescription = collection.name,
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.BottomCenter,
                                    placeholder = painterResource(R.drawable.img_collection_empty_cover),
                                    modifier = Modifier.size(73.907.dp, 108.397.dp).clip(RoundedCornerShape(6.569.dp)),
                                )
                            }
                            Text(
                                collection.name,
                                color = Gray300,
                                style = WebsosoTheme.typography.body5,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        } else if (isLoading) {
            Box(
                Modifier.fillMaxWidth().padding(20.dp),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(Modifier.size(24.dp)) }
        }
        if (isError) {
            TextButton(onClick = { viewModel.refresh(userId) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.collection_preview_retry))
            }
        }
    }
}
