package com.into.websoso.ui.notification.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.R
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White

@Composable
fun NotificationAppBar(
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(White)
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .height(44.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_notification_back),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .size(44.dp)
                .clickableWithoutRipple { onBackButtonClick() },
        )
        Text(
            text = stringResource(R.string.notification_notification),
            style = WebsosoTheme.typography.title2,
            color = Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 44.dp),
        )
    }
}

@Preview
@Composable
private fun NotificationAppBarPreview() {
    WebsosoTheme {
        NotificationAppBar(onBackButtonClick = {})
    }
}
