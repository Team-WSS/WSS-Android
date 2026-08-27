package com.into.websoso.ui.novelNotification.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary30
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_storage_null
import com.into.websoso.core.resource.R.string.novel_notification_empty
import com.into.websoso.core.resource.R.string.novel_notification_empty_explore

@Composable
fun NovelNotificationEmptyView(
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            imageVector = ImageVector.vectorResource(id = ic_storage_null),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(novel_notification_empty),
            style = WebsosoTheme.typography.body1,
            color = Gray200,
        )
        Spacer(modifier = Modifier.height(45.dp))
        Button(
            onClick = onExploreClick,
            modifier = Modifier.fillMaxWidth(0.5f),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary30),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            elevation = null,
        ) {
            Text(
                text = stringResource(novel_notification_empty_explore),
                style = WebsosoTheme.typography.title2,
                color = Primary100,
            )
        }
        Spacer(modifier = Modifier.weight(2f))
    }
}

@Preview
@Composable
private fun NovelNotificationEmptyViewPreview() {
    WebsosoTheme {
        NovelNotificationEmptyView(onExploreClick = {})
    }
}
