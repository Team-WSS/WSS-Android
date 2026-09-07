package com.into.websoso.feature.collection.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.into.websoso.core.designsystem.component.NetworkImage
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary20
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R
import com.into.websoso.domain.collection.model.Collection

@Composable
internal fun CollectionCard(
    collection: Collection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Primary20)
            .padding(horizontal = 17.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = collection.name.ellipsize(15),
                    color = Gray300,
                    style = WebsosoTheme.typography.title2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Icon(painterResource(R.drawable.ic_navigate_right), null, tint = Gray300, modifier = Modifier.size(24.dp))
            }
            Text(
                text = buildAnnotatedString {
                    collection.description?.takeIf(String::isNotBlank)?.let { append("${it.ellipsize(20)} · ") }
                    append(stringResource(R.string.collection_novels_label))
                    append(" ")
                    withStyle(SpanStyle(color = Primary100)) { append(collection.novelCount.toString()) }
                },
                color = Gray300,
                style = WebsosoTheme.typography.label2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        BoxWithConstraints(Modifier.fillMaxWidth().height(108.dp).clipToBounds()) {
            val coverWidth = 74.dp
            val step = ((maxWidth - coverWidth) / 4).coerceAtLeast(0.dp)
            repeat(5) { index ->
                val novel = collection.recentNovels.getOrNull(index)
                val coverModifier = Modifier
                    .offset(x = step * index)
                    .size(coverWidth, 108.dp)
                    .zIndex((5 - index).toFloat())
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
        if (!collection.isPublic) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_lock), null, tint = Gray200, modifier = Modifier.size(18.dp))
                Text(stringResource(R.string.collection_create_private), color = Gray200, style = WebsosoTheme.typography.label2)
            }
        }
    }
}

internal fun String.ellipsize(limit: Int): String = if (length > limit) take(limit) + "…" else this
