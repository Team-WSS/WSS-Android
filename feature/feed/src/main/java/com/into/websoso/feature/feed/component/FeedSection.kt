package com.into.websoso.feature.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.component.S3Image
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_link
import com.into.websoso.core.resource.R.drawable.ic_navigate_right
import com.into.websoso.core.resource.R.drawable.ic_star
import com.into.websoso.feature.feed.model.FeedUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun FeedSection(feeds: ImmutableList<FeedUiModel>) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        items(items = feeds) { feed ->
            FeedItem(
                feed = feed,
                onMoreClick = {},
            )
        }
    }
}

@Composable
private fun FeedItem(
    feed: FeedUiModel,
    onMoreClick: (feedId: Long) -> Unit,
) {
    Column(
        modifier = Modifier.padding(
            top = 20.dp,
            bottom = 10.dp,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            S3Image(
                imageUrl = feed.user.avatarImage,
                modifier = Modifier
                    .size(size = 32.dp)
                    .clip(shape = RoundedCornerShape(size = 10.dp)),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                modifier = Modifier.weight(weight = 1f),
            ) {
                Text(
                    text = feed.user.nickname,
                    style = WebsosoTheme.typography.title4,
                    color = Black,
                    modifier = Modifier.weight(weight = 1f, fill = false),
                )

                Text(
                    text = "·",
                    style = WebsosoTheme.typography.body5,
                    color = Gray200,
                    modifier = Modifier.padding(all = 3.dp),
                )

                Text(
                    text = feed.createdDate,
                    style = WebsosoTheme.typography.body5,
                    color = Gray200,
                )

                if (feed.isModified) {
                    Text(
                        text = "(수정됨)",
                        style = WebsosoTheme.typography.body5,
                        color = Gray200,
                    )
                }
            }

            Icon(
                imageVector = ImageVector.vectorResource(id = com.into.websoso.core.resource.R.drawable.ic_more),
                contentDescription = null,
                tint = Gray200,
                modifier = Modifier
                    .size(size = 14.dp)
                    .clickable { onMoreClick(feed.id) },
            )
        }

        Spacer(modifier = Modifier.height(height = 10.dp))

        Text(
            text = feed.content,
            style = WebsosoTheme.typography.body2,
            color = Black,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(height = 20.dp))

        FeedNovelInfo(novel = feed.novel)
    }
}

@Composable
private fun FeedNovelInfo(novel: FeedUiModel.NovelUiModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = novel.genre.boxColor,
                shape = RoundedCornerShape(size = 16.dp),
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = 6.dp),
                modifier = Modifier.weight(weight = 1f),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = ic_link),
                    contentDescription = null,
                    tint = novel.genre.iconColor,
                )

                Text(
                    text = novel.title,
                    style = WebsosoTheme.typography.title3,
                    color = Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(weight = 1f, fill = false),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = 6.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = ic_star),
                        contentDescription = null,
                    )

                    Text(
                        text = novel.rating.toString(),
                        style = WebsosoTheme.typography.label1,
                        color = Black,
                    )
                }

                Icon(
                    imageVector = ImageVector.vectorResource(id = ic_navigate_right),
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview
@Composable
private fun FeedSectionPreview() {
    WebsosoTheme {
        FeedSection(feeds = persistentListOf())
    }
}
