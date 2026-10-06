package com.into.websoso.feature.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R

@Composable
fun FeedLikeSyncNotice(
    isRestoreFailed: Boolean,
    needsRetry: Boolean,
    isSyncing: Boolean,
    onRetry: () -> Unit,
) {
    if (!isRestoreFailed && !needsRetry) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray50)
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(
                if (isRestoreFailed) R.string.feed_like_restore_failed_message else R.string.feed_like_unconfirmed_message,
            ),
            style = WebsosoTheme.typography.body4,
            color = Black,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onRetry, enabled = !isSyncing) {
            Text(text = stringResource(R.string.feed_like_retry))
        }
    }
}
