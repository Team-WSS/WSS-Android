package com.into.websoso.feature.collection.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.component.NetworkImage
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_library_half_star
import com.into.websoso.core.resource.R.drawable.ic_library_interesting
import com.into.websoso.core.resource.R.drawable.ic_library_null_star
import com.into.websoso.core.resource.R.drawable.ic_novel_detail_check
import com.into.websoso.core.resource.R.drawable.ic_novel_unselected
import com.into.websoso.core.resource.R.drawable.ic_storage_star
import com.into.websoso.feature.collection.model.CollectionLibraryNovelUiModel
import com.into.websoso.feature.collection.model.CollectionLibraryRatingStar
import com.into.websoso.feature.collection.model.CollectionLibraryReadStatus

@Composable
internal fun CollectionLibraryNovelItem(
    novel: CollectionLibraryNovelUiModel,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.toggleable(
            value = isSelected,
            role = Role.Checkbox,
            onValueChange = onSelectionChange,
        ),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(102.67f / 160f)
                .clip(RoundedCornerShape(8.dp)),
        ) {
            NetworkImage(
                imageUrl = novel.imageUrl,
                contentDescription = novel.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            novel.readStatus?.let { readStatus ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = 6.dp,
                            bottom = 7.dp,
                        ).size(
                            width = 48.dp,
                            height = 18.dp,
                        ).background(
                            color = readStatus.backgroundColor,
                            shape = RoundedCornerShape(4.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = readStatus.label,
                        color = White,
                        style = WebsosoTheme.typography.label2,
                    )
                }
            }
            if (novel.isInterested) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(ic_library_interesting),
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                    )
                }
            }
            Image(
                imageVector = ImageVector.vectorResource(
                    if (isSelected) ic_novel_detail_check else ic_novel_unselected,
                ),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = 8.dp,
                        end = 8.dp,
                    ).size(24.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = novel.title,
                color = Black,
                style = WebsosoTheme.typography.body4,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (novel.ratingStars.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    novel.ratingStars.forEach { star ->
                        Image(
                            imageVector = ImageVector.vectorResource(star.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(9.dp),
                        )
                    }
                }
            }
            novel.dateText?.let {
                Text(
                    text = it,
                    color = Gray200,
                    style = WebsosoTheme.typography.label2,
                )
            }
        }
    }
}

private val CollectionLibraryReadStatus.label: String
    get() = when (this) {
        CollectionLibraryReadStatus.WATCHING -> "보는 중"
        CollectionLibraryReadStatus.WATCHED -> "봤어요"
        CollectionLibraryReadStatus.QUIT -> "하차"
    }

private val CollectionLibraryReadStatus.backgroundColor: Color
    get() = when (this) {
        CollectionLibraryReadStatus.WATCHING -> Primary100
        CollectionLibraryReadStatus.WATCHED -> Black
        CollectionLibraryReadStatus.QUIT -> Gray200
    }

@get:DrawableRes
private val CollectionLibraryRatingStar.iconRes: Int
    get() = when (this) {
        CollectionLibraryRatingStar.FULL -> ic_storage_star
        CollectionLibraryRatingStar.HALF -> ic_library_half_star
        CollectionLibraryRatingStar.EMPTY -> ic_library_null_star
    }

@Preview(showBackground = true)
@Composable
private fun CollectionLibraryNovelItemPreview() {
    WebsosoTheme {
        CollectionLibraryNovelItem(
            novel = CollectionLibraryNovelUiModel(
                novelId = 1L,
                title = "당신의 이해를 돕기 위하여",
                imageUrl = "",
                readStatus = CollectionLibraryReadStatus.WATCHING,
                isInterested = true,
                ratingStars = List(5) { CollectionLibraryRatingStar.FULL },
                dateText = "24.01.03 ~ 25.03.08",
            ),
            isSelected = false,
            onSelectionChange = {},
            modifier = Modifier
                .padding(20.dp)
                .size(width = 103.dp, height = 240.dp),
        )
    }
}
