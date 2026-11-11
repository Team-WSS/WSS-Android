package com.into.websoso.feature.collection.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.string.collection_create_count
import com.into.websoso.core.resource.R.string.collection_create_description
import com.into.websoso.core.resource.R.string.collection_create_description_hint

private const val COLLECTION_DESCRIPTION_MAX_LENGTH = 60

@Composable
internal fun CollectionDescriptionInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(collection_create_description),
            color = Black,
            style = WebsosoTheme.typography.title2,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .background(
                    color = Gray50,
                    shape = RoundedCornerShape(14.dp),
                ).padding(
                    horizontal = 16.dp,
                    vertical = 18.dp,
                ),
        ) {
            BasicTextField(
                value = value,
                onValueChange = { changedValue ->
                    onValueChange(changedValue.take(COLLECTION_DESCRIPTION_MAX_LENGTH))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = WebsosoTheme.typography.body2.copy(color = Black),
                cursorBrush = SolidColor(Primary100),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(collection_create_description_hint),
                                color = Gray100,
                                style = WebsosoTheme.typography.body2,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Text(
                text = stringResource(
                    collection_create_count,
                    value.length,
                    COLLECTION_DESCRIPTION_MAX_LENGTH,
                ),
                color = Gray200,
                style = WebsosoTheme.typography.body2,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionDescriptionInputPreview() {
    WebsosoTheme {
        CollectionDescriptionInput(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
