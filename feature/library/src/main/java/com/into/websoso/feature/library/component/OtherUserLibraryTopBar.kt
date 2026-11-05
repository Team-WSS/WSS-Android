package com.into.websoso.feature.library.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_library_grid
import com.into.websoso.core.resource.R.drawable.ic_library_list
import com.into.websoso.core.resource.R.drawable.ic_library_sort
import com.into.websoso.core.resource.R.drawable.ic_navigate_left
import com.into.websoso.domain.library.model.SortCriteria

/**
 * 타유저 서재 상단 영역입니다.
 *
 * 내 서재와 달리 검색, 필터 칩, 알림 관리 없이 뒤로가기와 타이틀, 작품 개수, 정렬, 보기 전환만 보여줍니다.
 */
@Composable
internal fun OtherUserLibraryTopBar(
    totalCount: Long,
    sortCriteria: SortCriteria,
    isGrid: Boolean,
    onBackClick: () -> Unit,
    onSortClick: () -> Unit,
    onToggleViewType: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 6.dp)
                    .size(44.dp)
                    .debouncedClickable(onClick = onBackClick),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = ic_navigate_left),
                    contentDescription = null,
                )
            }

            Text(
                text = "서재",
                style = WebsosoTheme.typography.title2,
                color = Black,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "${totalCount}개",
                style = WebsosoTheme.typography.body4,
                color = Gray200,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatusBarAction(
                    iconRes = ic_library_sort,
                    iconSize = 16.dp,
                    text = sortCriteria.label,
                    onClick = onSortClick,
                )

                Box(
                    modifier = Modifier
                        .size(33.dp)
                        .debouncedClickable(onClick = onToggleViewType),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            id = if (isGrid) ic_library_grid else ic_library_list,
                        ),
                        contentDescription = null,
                        tint = Gray100,
                    )
                }
            }
        }
    }
}
