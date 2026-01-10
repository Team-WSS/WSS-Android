package com.into.websoso.feature.feed.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary50
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyFeedFilterModal() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "글 찾기 필터", style = WebsosoTheme.typography.body2, color = Gray200)
            Icon(
                modifier = Modifier,
                imageVector = ImageVector.vectorResource(R.drawable.ic_expanded_feed_image_close),
                contentDescription = "Close",
                tint = Gray300,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "장르", color = Black, style = WebsosoTheme.typography.title2)
        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val genres = listOf("로맨스", "로판", "판타지", "현판", "무협", "미스테리", "드라마", "라노벨", "BL")
            genres.forEach { genre ->
                GenreChip(text = genre, isSelected = (genre == "로맨스" || genre == "드라마"))
            }
        }

        Spacer(modifier = Modifier.height(42.dp))

        Text(text = "공개여부", color = Black, style = WebsosoTheme.typography.title2)
        Spacer(modifier = Modifier.height(16.dp))

        VisibilityItem(
            title = "공개글",
            icon = ImageVector.vectorResource(id = R.drawable.ic_visible),
            isSelected = true,
        )
        Divider(color = Gray50, thickness = 1.dp)
        VisibilityItem(
            title = "비공개글",
            icon = ImageVector.vectorResource(id = R.drawable.ic_unvisible),
            isSelected = false,
        )
        Divider(color = Gray50, thickness = 1.dp)

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { /* 필터 적용 */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary100),
        ) {
            Text(
                text = "해당하는 글 보기",
                style = WebsosoTheme.typography.title2,
                color = White,
            )
        }
    }
}

@Composable
private fun GenreChip(
    text: String,
    isSelected: Boolean,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isSelected) Primary100 else Gray50),
        color = if (isSelected) Primary50 else Gray50,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = if (isSelected) Primary100 else Gray300,
            style = WebsosoTheme.typography.body2,
        )
    }
}

@Composable
private fun VisibilityItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Gray200,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = WebsosoTheme.typography.body2,
            color = Gray200,
            modifier = Modifier.weight(1f),
        )

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (isSelected) Color(0xFF6559FF) else Color(0xFFE0E0E0),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    if (isSelected) R.drawable.ic_novel_detail_check else R.drawable.ic_novel_unselected,
                ),
                contentDescription = null,
                tint = Color.Transparent,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun MyFeedFilterModalPreview() {
    WebsosoTheme {
        MyFeedFilterModal()
    }
}
