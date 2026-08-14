package com.into.websoso.feature.collection.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.component.NetworkImage
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.LightPink
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Secondary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_collection_novel_add
import com.into.websoso.core.resource.R.drawable.ic_collection_novel_delete
import com.into.websoso.data.novel.model.NovelSearchEntity
import com.into.websoso.feature.collection.model.CollectionSelectedNovel

@Composable
internal fun CollectionNovelSearchItem(
    title: String,
    author: String,
    imageUrl: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NetworkImage(
            imageUrl = imageUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 73.dp, height = 98.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Black,
                style = WebsosoTheme.typography.title3,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (author.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = author,
                    color = Gray200,
                    style = WebsosoTheme.typography.body5,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        val isAdd = actionLabel == "추가"
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .background(if (isAdd) Primary100 else LightPink)
                .clickable(onClick = onActionClick)
                .padding(horizontal = 10.dp, vertical = 7.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isAdd) ic_collection_novel_add else ic_collection_novel_delete,
                    ),
                    tint = if (isAdd) White else Secondary100,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = actionLabel,
                    color = if (isAdd) White else Secondary100,
                    style = WebsosoTheme.typography.body5,
                )
            }
        }
    }
}

@Composable
internal fun CollectionNovelSearchItem(
    novel: NovelSearchEntity,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollectionNovelSearchItem(
        title = novel.title,
        author = novel.author,
        imageUrl = novel.imageUrl,
        actionLabel = "추가",
        onActionClick = onAddClick,
        modifier = modifier,
    )
}

@Composable
internal fun CollectionSelectedNovelItem(
    novel: CollectionSelectedNovel,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollectionNovelSearchItem(
        title = novel.title,
        author = novel.author,
        imageUrl = novel.imageUrl,
        actionLabel = "삭제",
        onActionClick = onDeleteClick,
        modifier = modifier,
    )
}
