package com.into.websoso.feature.collection.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme

private const val COLLECTION_NAME_MAX_LENGTH = 20

@Composable
internal fun CollectionNameInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                append("컬렉션 이름 ")
                withStyle(style = SpanStyle(color = Primary100)) {
                    append("*")
                }
            },
            color = Black,
            style = WebsosoTheme.typography.title2,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(
                    color = Gray50,
                    shape = RoundedCornerShape(12.dp),
                ).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = value,
                onValueChange = { changedValue ->
                    onValueChange(changedValue.take(COLLECTION_NAME_MAX_LENGTH))
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = WebsosoTheme.typography.body2.copy(color = Black),
                cursorBrush = SolidColor(Primary100),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = "컬렉션 이름을 입력해주세요",
                                color = Gray100,
                                style = WebsosoTheme.typography.body2,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Text(
                text = "(${value.length}/$COLLECTION_NAME_MAX_LENGTH)",
                color = Gray200,
                style = WebsosoTheme.typography.body2,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionNameInputPreview() {
    WebsosoTheme {
        CollectionNameInput(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
