package com.into.websoso.feature.collection.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray70New
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_common_search
import com.into.websoso.core.resource.R.drawable.ic_common_search_clear

@Composable
internal fun CollectionNovelSearchField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .border(
                width = 1.dp,
                color = Gray70New,
                shape = RoundedCornerShape(14.dp),
            ).padding(
                start = 16.dp,
                end = 10.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            singleLine = true,
            textStyle = WebsosoTheme.typography.body4.copy(color = Black),
            cursorBrush = SolidColor(Primary100),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchClick() }),
            decorationBox = { innerTextField ->
                Box {
                    if (value.text.isEmpty()) {
                        Text(
                            text = "작품 제목, 작가를 검색하세요",
                            color = Gray100,
                            style = WebsosoTheme.typography.body4,
                        )
                    }
                    innerTextField()
                }
            },
        )
        if (value.text.isNotEmpty()) {
            IconButton(
                onClick = onClearClick,
                modifier = Modifier.size(36.dp),
            ) {
                Image(
                    painter = painterResource(id = ic_common_search_clear),
                    contentDescription = "검색어 지우기",
                    modifier = Modifier.size(36.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clickable(
                    onClick = onSearchClick,
                    role = Role.Button,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = ic_common_search),
                contentDescription = "작품 검색",
                modifier = Modifier.size(width = 25.dp, height = 26.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionNovelSearchFieldPreview() {
    WebsosoTheme {
        CollectionNovelSearchField(
            value = TextFieldValue(),
            onValueChange = {},
            onClearClick = {},
            onSearchClick = {},
            focusRequester = remember { FocusRequester() },
            modifier = Modifier.padding(20.dp),
        )
    }
}
