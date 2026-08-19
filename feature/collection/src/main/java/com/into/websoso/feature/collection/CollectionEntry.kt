package com.into.websoso.feature.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R

@Composable
fun CollectionEntry(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    collectionCount: Int = 0,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(White)
            .debouncedClickable(onClick = onClick)
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = buildAnnotatedString {
                append("컬렉션 ")
                withStyle(style = SpanStyle(color = Primary100)) {
                    append(collectionCount.toString())
                }
                append("개")
            },
            color = Gray300,
            style = WebsosoTheme.typography.title2,
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.btn_setting_right),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionEntryPreview() {
    WebsosoTheme {
        CollectionEntry(onClick = {})
    }
}
