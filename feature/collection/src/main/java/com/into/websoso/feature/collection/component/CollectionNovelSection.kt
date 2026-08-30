package com.into.websoso.feature.collection.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.component.NetworkImage
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_library_sort_check
import com.into.websoso.core.resource.R.drawable.ic_plus_novel
import com.into.websoso.core.resource.R.string.collection_create_add_novel
import com.into.websoso.core.resource.R.string.collection_create_count
import com.into.websoso.core.resource.R.string.collection_create_edit_novel
import com.into.websoso.core.resource.R.string.collection_create_novel_list
import com.into.websoso.core.resource.R.string.collection_create_representative
import com.into.websoso.feature.collection.model.CollectionSelectedNovel

@Composable
internal fun CollectionNovelSection(
    selectedNovels: List<CollectionSelectedNovel>,
    representativeNovelId: Long?,
    onRepresentativeNovelClick: (Long) -> Unit,
    onAddNovelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val novelList = stringResource(collection_create_novel_list)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = buildAnnotatedString {
                    append("$novelList ")
                    withStyle(style = SpanStyle(color = Primary100)) {
                        append("*")
                    }
                },
                color = Black,
                style = WebsosoTheme.typography.title2,
            )
            Text(
                text = stringResource(collection_create_count, selectedNovels.size, 100),
                color = Gray200,
                style = WebsosoTheme.typography.body3,
            )
        }
        if (selectedNovels.isEmpty()) {
            CollectionNovelEditCard(
                label = stringResource(collection_create_add_novel),
                onClick = onAddNovelClick,
                modifier = Modifier.size(width = 103.dp, height = 160.dp),
            )
        } else {
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                (listOf<CollectionSelectedNovel?>(null) + selectedNovels.asReversed())
                    .chunked(3)
                    .forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rowItems.forEach { novel ->
                                if (novel == null) {
                                    CollectionNovelEditCard(
                                        label = stringResource(collection_create_edit_novel),
                                        onClick = onAddNovelClick,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(156.dp),
                                    )
                                } else {
                                    CollectionNovelItem(
                                        novel = novel,
                                        isRepresentative = novel.novelId == representativeNovelId,
                                        onRepresentativeClick = {
                                            onRepresentativeNovelClick(novel.novelId)
                                        },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
            }
        }
    }
}

@Composable
private fun CollectionNovelEditCard(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(
                color = Gray50,
                shape = shape,
            ).clickable(
                onClick = onClick,
                role = Role.Button,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = label,
            color = Gray200,
            style = WebsosoTheme.typography.title4,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Icon(
            painter = painterResource(id = ic_plus_novel),
            contentDescription = null,
            tint = Gray200,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun CollectionNovelItem(
    novel: CollectionSelectedNovel,
    isRepresentative: Boolean,
    onRepresentativeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val thumbnailShape = RoundedCornerShape(8.dp)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(156.dp)
                .clip(thumbnailShape)
                .selectable(
                    selected = isRepresentative,
                    onClick = onRepresentativeClick,
                    role = Role.RadioButton,
                ).then(
                    if (isRepresentative) {
                        Modifier.border(
                            width = 2.dp,
                            color = Primary100,
                            shape = thumbnailShape,
                        )
                    } else {
                        Modifier
                    },
                ),
        ) {
            NetworkImage(
                imageUrl = novel.imageUrl,
                contentDescription = novel.title,
                contentScale = ContentScale.Crop,
                // TODO: 기획·디자인 확인 후 Alignment.Center 적용 여부 재검토
                alignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxSize(),
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .height(18.dp)
                    .background(
                        color = if (isRepresentative) Primary100 else Gray100,
                        shape = RoundedCornerShape(4.dp),
                    ).padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isRepresentative) {
                    Icon(
                        painter = painterResource(id = ic_library_sort_check),
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(10.dp),
                    )
                }
                Text(
                    text = stringResource(collection_create_representative),
                    color = White,
                    style = WebsosoTheme.typography.label2,
                )
            }
        }
        Text(
            text = novel.title,
            color = Black,
            style = WebsosoTheme.typography.body4,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionNovelSectionPreview() {
    WebsosoTheme {
        CollectionNovelSection(
            selectedNovels = emptyList(),
            representativeNovelId = null,
            onRepresentativeNovelClick = {},
            onAddNovelClick = {},
        )
    }
}
