package com.into.websoso.feature.collection.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_plus_novel
import com.into.websoso.core.resource.R.string.collection_create_add_novel
import com.into.websoso.core.resource.R.string.collection_create_count
import com.into.websoso.core.resource.R.string.collection_create_novel_list

@Composable
internal fun CollectionNovelSection(
    onAddNovelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val addCardShape = RoundedCornerShape(8.dp)
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
                text = stringResource(collection_create_count, 0, 100),
                color = Gray200,
                style = WebsosoTheme.typography.body3,
            )
        }
        Column(
            modifier = Modifier
                .size(width = 103.dp, height = 160.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    color = Gray50,
                    shape = RoundedCornerShape(8.dp),
                ).clickable(
                    onClick = onAddNovelClick,
                    role = Role.Button,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(collection_create_add_novel),
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
}

@Preview(showBackground = true)
@Composable
private fun CollectionNovelSectionPreview() {
    WebsosoTheme {
        CollectionNovelSection(onAddNovelClick = {})
    }
}
