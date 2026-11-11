package com.into.websoso.feature.collection.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_lock
import com.into.websoso.core.resource.R.string.collection_create_private

@Composable
internal fun CollectionPrivacySetting(
    isPrivate: Boolean,
    onPrivateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Gray300)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = ic_lock),
            contentDescription = null,
            tint = Gray200,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = stringResource(collection_create_private),
            color = Gray50,
            style = WebsosoTheme.typography.title3,
            modifier = Modifier
                .padding(start = 4.dp)
                .weight(1f),
        )
        CollectionPrivacySwitch(
            checked = isPrivate,
            onCheckedChange = onPrivateChange,
        )
    }
}

@Composable
private fun CollectionPrivacySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 18.dp else 0.dp,
        animationSpec = tween(durationMillis = 150),
        label = "CollectionPrivacySwitchThumbOffset",
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) Primary100 else Gray100,
        animationSpec = tween(durationMillis = 150),
        label = "CollectionPrivacySwitchTrackColor",
    )
    Box(
        modifier = Modifier
            .size(42.dp)
            .toggleable(
                value = checked,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 22.dp)
                .background(
                    color = trackColor,
                    shape = RoundedCornerShape(11.dp),
                ).padding(2.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .offset(x = thumbOffset)
                    .background(
                        color = White,
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionPrivacySettingPreview() {
    WebsosoTheme {
        CollectionPrivacySetting(
            isPrivate = false,
            onPrivateChange = {},
        )
    }
}
