package com.into.websoso.feature.collection.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
internal fun CollectionNetworkError(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(img_load_fail),
            contentDescription = null,
            modifier = Modifier.size(width = 166.dp, height = 160.dp),
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(load_fail_title),
            color = Black,
            style = WebsosoTheme.typography.title1,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(load_fail_description),
            color = Gray300,
            style = WebsosoTheme.typography.body2,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onRetryClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary100),
            contentPadding = PaddingValues(horizontal = 38.dp, vertical = 14.dp),
            elevation = null,
        ) {
            Text(
                text = stringResource(load_fail_reload),
                color = White,
                style = WebsosoTheme.typography.label1,
            )
        }
    }
}
