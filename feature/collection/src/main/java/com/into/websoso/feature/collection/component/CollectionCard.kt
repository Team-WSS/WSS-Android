package com.into.websoso.feature.collection.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.designsystem.component.NetworkImage
import com.into.websoso.core.designsystem.component.S3Image
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary20
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R
import com.into.websoso.domain.collection.model.Collection
import com.into.websoso.domain.collection.model.CollectionNovel
import com.into.websoso.domain.collection.model.CollectionOwner
import com.into.websoso.domain.collection.model.PublicCollection

private const val LIST_COVER_COUNT = 5
private const val HOME_COVER_COUNT = 4

@Composable
internal fun CollectionCard(
    collection: Collection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollectionCardContainer(modifier.fillMaxWidth()) {
        CollectionCardHeader(
            name = collection.name,
            description = collection.description,
            novelCount = collection.novelCount,
            modifier = Modifier.debouncedClickable(role = Role.Button, onClick = onClick),
        )
        CollectionCovers(collection.recentNovels, LIST_COVER_COUNT)
        if (!collection.isPublic) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_lock), null, tint = Gray200, modifier = Modifier.size(18.dp))
                Text(stringResource(R.string.collection_create_private), color = Gray200, style = WebsosoTheme.typography.label2)
            }
        }
    }
}

/** 전체 컬렉션뷰 카드. 프로필 영역을 포함해 카드 어디를 눌러도 상세로 이동한다. */
@Composable
internal fun PublicCollectionCard(
    collection: PublicCollection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollectionCardContainer(
        modifier.fillMaxWidth().debouncedClickable(role = Role.Button, onClick = onClick),
    ) {
        CollectionCardHeader(collection.name, collection.description, collection.novelCount)
        CollectionCovers(collection.recentNovels, LIST_COVER_COUNT)
        CollectionOwnerLabel(collection.owner)
    }
}

/** 홈 컬렉션 섹션 카드. 표지는 최대 4개까지 보여준다. */
@Composable
internal fun HomeCollectionCard(
    collection: PublicCollection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollectionCardContainer(
        modifier = modifier.width(284.dp).debouncedClickable(role = Role.Button, onClick = onClick),
        horizontalPadding = 16.dp,
        spacing = 12.dp,
    ) {
        CollectionCardHeader(
            name = collection.name,
            description = collection.description,
            novelCount = collection.novelCount,
            showArrow = false,
            descriptionStyle = WebsosoTheme.typography.label1,
        )
        CollectionCovers(collection.recentNovels, HOME_COVER_COUNT)
        CollectionOwnerLabel(collection.owner)
    }
}

@Composable
private fun CollectionCardContainer(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 17.dp,
    spacing: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Primary20)
            .padding(horizontal = horizontalPadding, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content,
    )
}

@Composable
private fun CollectionCardHeader(
    name: String,
    description: String?,
    novelCount: Int,
    modifier: Modifier = Modifier,
    showArrow: Boolean = true,
    descriptionStyle: TextStyle = WebsosoTheme.typography.label2,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name.ellipsize(15),
                color = Gray300,
                style = WebsosoTheme.typography.title2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (showArrow) {
                Icon(painterResource(R.drawable.ic_navigate_right), null, tint = Gray300, modifier = Modifier.size(24.dp))
            }
        }
        Text(
            text = buildAnnotatedString {
                description?.takeIf(String::isNotBlank)?.let { append("${it.ellipsize(20)} · ") }
                append(stringResource(R.string.collection_novels_label))
                append(" ")
                withStyle(SpanStyle(color = Primary100)) { append(novelCount.toString()) }
            },
            color = Gray300,
            style = descriptionStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** 표지를 겹쳐 [coverCount]칸 보여주고, 작품이 모자라면 빈 표지로 채운다. */
@Composable
private fun CollectionCovers(
    novels: List<CollectionNovel>,
    coverCount: Int,
) {
    BoxWithConstraints(Modifier.fillMaxWidth().height(108.dp).clipToBounds()) {
        val coverWidth = 74.dp
        val step = ((maxWidth - coverWidth) / (coverCount - 1)).coerceAtLeast(0.dp)
        repeat(coverCount) { index ->
            val novel = novels.getOrNull(index)
            val coverModifier = Modifier
                .offset(x = step * index)
                .size(coverWidth, 108.dp)
                .zIndex((coverCount - index).toFloat())
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
            if (novel == null) {
                Image(painterResource(R.drawable.img_collection_empty_cover), null, coverModifier, contentScale = ContentScale.Crop)
            } else {
                NetworkImage(
                    imageUrl = novel.imageUrl,
                    contentDescription = novel.title,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.BottomCenter,
                    placeholder = painterResource(R.drawable.img_collection_empty_cover),
                    modifier = coverModifier,
                )
            }
        }
    }
}

@Composable
private fun CollectionOwnerLabel(owner: CollectionOwner) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        S3Image(
            imageUrl = owner.avatarImageUrl,
            modifier = Modifier.size(16.dp).clip(RoundedCornerShape(5.dp)),
        )
        Text(
            text = stringResource(R.string.collection_owner_label, owner.nickname),
            color = Gray200,
            style = WebsosoTheme.typography.label2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

internal fun String.ellipsize(limit: Int): String = if (length > limit) take(limit) + "…" else this
