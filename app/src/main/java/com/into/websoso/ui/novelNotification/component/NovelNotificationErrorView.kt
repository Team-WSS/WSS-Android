package com.into.websoso.ui.novelNotification.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.img_load_fail
import com.into.websoso.core.resource.R.string.load_fail_description
import com.into.websoso.core.resource.R.string.load_fail_reload
import com.into.websoso.core.resource.R.string.load_fail_title

@Composable
fun NovelNotificationErrorView(
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = img_load_fail),
            contentDescription = null,
            modifier = Modifier.size(width = 166.dp, height = 160.dp),
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(load_fail_title),
            style = WebsosoTheme.typography.title1,
            color = Black,
            textAlign = Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(load_fail_description),
            style = WebsosoTheme.typography.body2,
            color = Gray300,
            textAlign = Center,
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(load_fail_reload),
            style = WebsosoTheme.typography.label1,
            color = White,
            textAlign = Center,
            modifier = Modifier
                .background(color = Primary100, shape = RoundedCornerShape(8.dp))
                .clickableWithoutRipple { onReloadClick() }
                .padding(horizontal = 38.dp, vertical = 14.dp),
        )
        Spacer(modifier = Modifier.weight(2f))
    }
}

@Preview
@Composable
private fun NovelNotificationErrorViewPreview() {
    WebsosoTheme {
        NovelNotificationErrorView(onReloadClick = {})
    }
}
