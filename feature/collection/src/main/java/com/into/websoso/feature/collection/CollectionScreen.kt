package com.into.websoso.feature.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.feature.collection.component.CollectionAppBar
import com.into.websoso.feature.collection.component.CollectionTabRow

@Composable
fun CollectionScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding(),
    ) {
        CollectionAppBar(onNavigateBack = onNavigateBack)
        CollectionTabRow()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "컬렉션",
                    color = Black,
                    style = WebsosoTheme.typography.headline1,
                )
                Text(
                    text = "임시화면",
                    color = Gray200,
                    style = WebsosoTheme.typography.body2,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionScreenPreview() {
    WebsosoTheme {
        CollectionScreen(onNavigateBack = {})
    }
}
