package com.into.websoso.feature.collection.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Black60
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Secondary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R

@Composable
internal fun CollectionConfirmDialog(
    isDelete: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.widthIn(max = 292.dp).dropShadow(
                shape = RoundedCornerShape(12.dp),
                shadow = Shadow(radius = 15.dp, color = Black60.copy(alpha = 0.1f), offset = DpOffset(0.dp, 2.dp)),
            ),
            shape = RoundedCornerShape(12.dp),
            color = White,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Image(painterResource(R.drawable.ic_collection_warning), null, Modifier.size(46.dp))
                Text(
                    stringResource(if (isDelete) R.string.collection_delete_title else R.string.collection_discard_title),
                    style = WebsosoTheme.typography.title2,
                    color = Black,
                    textAlign = TextAlign.Center,
                )
                if (isDelete) {
                    Text(stringResource(R.string.collection_delete_description), style = WebsosoTheme.typography.body3, color = Gray300)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = if (isDelete) onDismiss else onConfirm,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gray50, contentColor = Gray300),
                    ) {
                        Text(
                            stringResource(if (isDelete) R.string.collection_cancel else R.string.collection_discard),
                            style = WebsosoTheme.typography.body3,
                        )
                    }
                    Button(
                        onClick = if (isDelete) onConfirm else onDismiss,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDelete) Secondary100 else Primary100,
                            contentColor = White,
                        ),
                    ) {
                        Text(
                            stringResource(if (isDelete) R.string.collection_create_delete else R.string.collection_continue),
                            style = WebsosoTheme.typography.body3,
                        )
                    }
                }
            }
        }
    }
}
