package com.into.websoso.feature.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.designsystem.component.NetworkImage
import com.into.websoso.core.designsystem.component.S3Image
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.GrayToast
import com.into.websoso.core.designsystem.theme.Secondary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R
import com.into.websoso.core.resource.R.drawable.ic_link
import com.into.websoso.core.resource.R.drawable.ic_navigate_right
import com.into.websoso.core.resource.R.drawable.ic_star
import com.into.websoso.feature.feed.model.FeedTab
import com.into.websoso.feature.feed.model.FeedUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun FeedSection(
    currentTab: FeedTab,
    feeds: ImmutableList<FeedUiModel>,
    onProfileClick: (userId: Long, feedTab: FeedTab) -> Unit,
    onMoreClick: (feedId: Long) -> Unit,
    onNovelClick: (novelId: Long) -> Unit,
    onLikeClick: (feedId: Long) -> Unit,
    onContentClick: (feedId: Long, isLiked: Boolean) -> Unit,
    onFirstItemClick: (feedId: Long, isMyFeed: Boolean) -> Unit,
    onSecondItemClick: (feedId: Long, isMyFeed: Boolean) -> Unit,
    onRefreshPull: () -> Unit,
    isRefreshing: Boolean,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefreshPull,
        modifier = Modifier.fillMaxSize(),
    ) {
        if (feeds.isEmpty()) {
            FeedEmptyCase()
        } else {
            LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
                itemsIndexed(items = feeds) { index, feed ->
                    FeedItem(
                        feed = feed,
                        currentTab = currentTab,
                        onMoreClick = onMoreClick,
                        onProfileClick = onProfileClick,
                        onNovelClick = onNovelClick,
                        onLikeClick = onLikeClick,
                        onContentClick = onContentClick,
                        onFirstItemClick = onFirstItemClick,
                        onSecondItemClick = onSecondItemClick,
                    )

                    if (index < feeds.lastIndex) HorizontalDivider(color = Gray50)
                }
            }
        }
    }
}

@Composable
private fun FeedItem(
    feed: FeedUiModel,
    currentTab: FeedTab,
    onProfileClick: (userId: Long, feedTab: FeedTab) -> Unit,
    onMoreClick: (feedId: Long) -> Unit,
    onNovelClick: (novelId: Long) -> Unit,
    onLikeClick: (feedId: Long) -> Unit,
    onContentClick: (feedId: Long, isLiked: Boolean) -> Unit,
    onFirstItemClick: (feedId: Long, isMyFeed: Boolean) -> Unit,
    onSecondItemClick: (feedId: Long, isMyFeed: Boolean) -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(
            top = 20.dp,
            bottom = 10.dp,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .debouncedClickable {
                    onProfileClick(feed.user.id, currentTab)
                },
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
            Box {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_three_dots),
                    contentDescription = null,
                    tint = Gray200,
                    modifier = Modifier
                        .size(size = 14.dp)
                        .debouncedClickable {
                            isMenuExpanded = true
                            onMoreClick(feed.id)
                        },
                )

                if (isMenuExpanded) {
                    FeedMoreMenu(
                        isMyFeed = feed.isMyFeed,
                        onDismissRequest = { isMenuExpanded = false },
                        onFirstItemClick = {
                            onFirstItemClick(feed.id, currentTab == FeedTab.MY_FEED)
                        },
                        onSecondItemClick = {
                            onSecondItemClick(feed.id, currentTab == FeedTab.MY_FEED)
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(height = 10.dp))

        if (feed.isSpoiler) {
            Text(
                text = "스포일러가 포함된 글 보기",
                style = WebsosoTheme.typography.body2,
                color = Secondary100,
                modifier = Modifier.debouncedClickable { onContentClick(feed.id, feed.isLiked) },
            )
        } else {
            Text(
                text = feed.content,
                style = WebsosoTheme.typography.body2,
                color = Black,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.debouncedClickable { onContentClick(feed.id, feed.isLiked) },
            )
        }

        Spacer(modifier = Modifier.height(height = 20.dp))

        if (feed.imageUrls.isNotEmpty()) {
            Box {
                NetworkImage(
                    imageUrl = feed.imageUrls.firstOrNull().orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = 334f / 237f)
                        .clip(RoundedCornerShape(size = 14.dp)),
                    contentScale = ContentScale.Crop,
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(alignment = Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 10.dp)
                        .size(size = 20.dp)
                        .background(
                            color = GrayToast,
                            shape = CircleShape,
                        ),
                ) {
                    Text(
                        text = feed.imageUrls.size.toString(),
                        style = WebsosoTheme.typography.body5,
                        color = White,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.height(height = 10.dp))
        }

        if (feed.novel != null) {
            FeedNovelInfo(
                novel = feed.novel,
                onNovelClick = onNovelClick,
            )
        }

        Spacer(modifier = Modifier.height(height = 10.dp))

        when (currentTab) {
            FeedTab.MY_FEED -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_lock),
                        contentDescription = null,
                        tint = Gray200,
                    )

                    Text(
                        text = "나만 보는 기록이에요.",
                        style = WebsosoTheme.typography.body4,
                        color = Gray200,
                    )
                }
            }

            FeedTab.SOSO_FEED -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.debouncedClickable { onLikeClick(feed.id) },
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                id = if (feed.isLiked) R.drawable.ic_thumb_up_on else R.drawable.ic_thumb_up,
                            ),
                            tint = if (feed.isLiked) Black else Gray200,
                            contentDescription = null,
                        )

                        Text(
                            text = feed.likeCount.toString(),
                            style = WebsosoTheme.typography.title3,
                            color = if (feed.isLiked) Black else Gray200,
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_comment),
                            contentDescription = null,
                            tint = Gray200,
                        )

                        Text(
                            text = feed.commentCount.toString(),
                            style = WebsosoTheme.typography.title3,
                            color = Gray200,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedNovelInfo(
    novel: FeedUiModel.NovelUiModel,
    onNovelClick: (novelId: Long) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = novel.genre.boxColor,
                shape = RoundedCornerShape(size = 16.dp),
            )
            .debouncedClickable {
                onNovelClick(novel.id)
            },
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
                novel.feedWriterNovelRating?.let {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = White),
        ) {
            FeedSection(
                feeds = persistentListOf(FeedUiModel()),
                currentTab = FeedTab.SOSO_FEED,
                onProfileClick = { _, _ -> },
                onMoreClick = { },
                onNovelClick = { },
                onLikeClick = { },
                onContentClick = { _, _ -> },
                onFirstItemClick = { _, _ -> },
                onSecondItemClick = { _, _ -> },
                onRefreshPull = { },
                isRefreshing = false,
            )
        }
    }
}
