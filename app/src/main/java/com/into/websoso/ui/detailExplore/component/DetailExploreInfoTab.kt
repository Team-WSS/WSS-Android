package com.into.websoso.ui.detailExplore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary30
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_home_info_circle
import com.into.websoso.core.resource.R.string.detail_explore_info_genre
import com.into.websoso.core.resource.R.string.detail_explore_info_platform
import com.into.websoso.core.resource.R.string.detail_explore_info_platform_tooltip
import com.into.websoso.core.resource.R.string.detail_explore_info_rating
import com.into.websoso.core.resource.R.string.detail_explore_info_rating_range
import com.into.websoso.core.resource.R.string.detail_explore_info_rating_value
import com.into.websoso.core.resource.R.string.detail_explore_info_status
import com.into.websoso.core.resource.R.string.detail_explore_info_status_complete
import com.into.websoso.core.resource.R.string.detail_explore_info_status_in_series
import com.into.websoso.ui.detailExplore.DetailExploreViewModel
import com.into.websoso.ui.detailExplore.DetailExploreViewModel.Companion.RATING_MAX
import com.into.websoso.ui.detailExplore.DetailExploreViewModel.Companion.RATING_MIN
import com.into.websoso.ui.detailExplore.DetailExploreViewModel.Companion.RATING_STEP
import com.into.websoso.ui.detailExplore.info.model.Genre
import com.into.websoso.ui.detailExplore.info.model.Platform
import com.into.websoso.ui.detailExplore.info.model.SeriesStatus
import kotlinx.coroutines.launch

@Composable
fun DetailExploreInfoTab(
    viewModel: DetailExploreViewModel,
    modifier: Modifier = Modifier,
) {
    val selectedGenres by viewModel.selectedGenres.observeAsState(emptyList())
    val selectedPlatforms by viewModel.selectedPlatforms.observeAsState(emptyList())
    val selectedStatus by viewModel.selectedStatus.observeAsState(null)
    val ratingMin by viewModel.selectedRatingMin.observeAsState(RATING_MIN)
    val ratingMax by viewModel.selectedRatingMax.observeAsState(RATING_MAX)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        GenreSection(
            selectedGenres = selectedGenres,
            onGenreClick = viewModel::updateSelectedGenres,
        )
        PlatformSection(
            selectedPlatforms = selectedPlatforms,
            onPlatformClick = viewModel::updateSelectedPlatforms,
        )
        StatusSection(
            selectedStatus = selectedStatus,
            onStatusClick = { status ->
                viewModel.updateSelectedSeriesStatus(
                    if (selectedStatus == status) null else status,
                )
            },
        )
        RatingSection(
            min = ratingMin,
            max = ratingMax,
            onRangeChange = viewModel::updateSelectedRatingRange,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            style = WebsosoTheme.typography.title2,
            color = Black,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlatformSectionTitle() {
    val tooltipState = rememberTooltipState()
    val coroutineScope = rememberCoroutineScope()
    val positionProvider = rememberPlatformTooltipPositionProvider()
    val tooltipText = stringResource(detail_explore_info_platform_tooltip)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(detail_explore_info_platform),
            style = WebsosoTheme.typography.title2,
            color = Black,
        )
        Spacer(modifier = Modifier.width(10.dp))
        TooltipBox(
            positionProvider = positionProvider,
            tooltip = {
                Box(
                    modifier = Modifier
                        .size(width = 180.dp, height = 28.dp)
                        .background(
                            color = Primary30,
                            shape = PlatformTooltipShape(
                                cornerRadius = 11.3.dp,
                                caretWidth = 7.dp,
                                caretHeight = 9.dp,
                            ),
                        ).padding(start = 18.dp, end = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = tooltipText,
                        style = WebsosoTheme.typography.body5,
                        color = Primary100,
                    )
                }
            },
            state = tooltipState,
            enableUserInput = false,
        ) {
            Icon(
                painter = painterResource(ic_home_info_circle),
                contentDescription = tooltipText,
                tint = Gray300,
                modifier = Modifier
                    .size(18.dp)
                    .clickableWithoutRipple {
                        coroutineScope.launch { tooltipState.show() }
                    },
            )
        }
    }
}

private class PlatformTooltipShape(
    private val cornerRadius: Dp,
    private val caretWidth: Dp,
    private val caretHeight: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val radius = with(density) { cornerRadius.toPx() }
            .coerceAtMost(size.height / 2)
        val caretWidthPx = with(density) { caretWidth.toPx() }
        val halfCaretHeight = with(density) { caretHeight.toPx() } / 2
        val bodyLeft = caretWidthPx
        val bodyRight = size.width
        val centerY = size.height / 2

        return Outline.Generic(
            Path().apply {
                moveTo(bodyLeft + radius, 0f)
                lineTo(bodyRight - radius, 0f)
                quadraticTo(bodyRight, 0f, bodyRight, radius)
                lineTo(bodyRight, size.height - radius)
                quadraticTo(bodyRight, size.height, bodyRight - radius, size.height)
                lineTo(bodyLeft + radius, size.height)
                quadraticTo(bodyLeft, size.height, bodyLeft, size.height - radius)
                lineTo(bodyLeft, centerY + halfCaretHeight)
                lineTo(0f, centerY)
                lineTo(bodyLeft, centerY - halfCaretHeight)
                lineTo(bodyLeft, radius)
                quadraticTo(bodyLeft, 0f, bodyLeft + radius, 0f)
                close()
            },
        )
    }
}

@Composable
private fun rememberPlatformTooltipPositionProvider(): PopupPositionProvider {
    val density = LocalDensity.current
    val spacing = with(density) { 6.dp.roundToPx() }

    return remember(spacing) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
            ): IntOffset {
                val x = anchorBounds.right + spacing
                val y = anchorBounds.top + (anchorBounds.height - popupContentSize.height) / 2

                return IntOffset(
                    x = x.coerceAtMost(windowSize.width - popupContentSize.width),
                    y = y.coerceIn(0, windowSize.height - popupContentSize.height),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreSection(
    selectedGenres: List<Genre>,
    onGenreClick: (Genre) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionTitle(text = stringResource(detail_explore_info_genre))
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Genre.entries.forEach { genre ->
                SelectableTagChip(
                    label = genre.titleKr,
                    isSelected = selectedGenres.contains(genre),
                    onClick = { onGenreClick(genre) },
                    modifier = Modifier.size(
                        width = genre.figmaWidthDp.dp,
                        height = 37.dp,
                    ),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlatformSection(
    selectedPlatforms: List<Platform>,
    onPlatformClick: (Platform) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        PlatformSectionTitle()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PlatformChipRow(
                platforms = Platform.entries.take(3),
                selectedPlatforms = selectedPlatforms,
                onPlatformClick = onPlatformClick,
            )
            PlatformChipRow(
                platforms = Platform.entries.drop(3),
                selectedPlatforms = selectedPlatforms,
                onPlatformClick = onPlatformClick,
            )
        }
    }
}

@Composable
private fun PlatformChipRow(
    platforms: List<Platform>,
    selectedPlatforms: List<Platform>,
    onPlatformClick: (Platform) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        platforms.forEach { platform ->
            SelectableTagChip(
                label = platform.displayName,
                isSelected = selectedPlatforms.contains(platform),
                onClick = { onPlatformClick(platform) },
                modifier = Modifier.size(
                    width = platform.chipWidthDp.dp,
                    height = 37.dp,
                ),
            )
        }
    }
}

@Composable
private fun StatusSection(
    selectedStatus: SeriesStatus?,
    onStatusClick: (SeriesStatus) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionTitle(text = stringResource(detail_explore_info_status))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatusChipCell(
                label = stringResource(detail_explore_info_status_in_series),
                status = SeriesStatus.IN_SERIES,
                selectedStatus = selectedStatus,
                onClick = onStatusClick,
                modifier = Modifier.weight(1f),
            )
            StatusChipCell(
                label = stringResource(detail_explore_info_status_complete),
                status = SeriesStatus.COMPLETED,
                selectedStatus = selectedStatus,
                onClick = onStatusClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatusChipCell(
    label: String,
    status: SeriesStatus,
    selectedStatus: SeriesStatus?,
    onClick: (SeriesStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    SelectableStatusChip(
        label = label,
        isSelected = selectedStatus == status,
        onClick = { onClick(status) },
        modifier = modifier.aspectRatio(STATUS_CHIP_ASPECT_RATIO),
    )
}

private const val STATUS_CHIP_ASPECT_RATIO = 155f / 43f

@Composable
private fun RatingSection(
    min: Float,
    max: Float,
    onRangeChange: (Float, Float) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(detail_explore_info_rating),
                style = WebsosoTheme.typography.title2,
                color = Black,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(detail_explore_info_rating_range, min, max),
                style = WebsosoTheme.typography.body2,
                color = Primary100,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            RatingValueBox(value = min)
            Box(modifier = Modifier.weight(1f)) {
                RatingRangeSlider(
                    min = min,
                    max = max,
                    valueRange = RATING_MIN..RATING_MAX,
                    stepSize = RATING_STEP,
                    onValueChange = onRangeChange,
                )
            }
            RatingValueBox(value = max)
        }
        Spacer(modifier = Modifier.height(38.dp))
    }
}

@Composable
private fun RatingValueBox(value: Float) {
    Box(
        modifier = Modifier
            .size(width = 50.dp, height = 38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Gray50),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(detail_explore_info_rating_value, value),
            style = WebsosoTheme.typography.body2,
            color = Primary100,
        )
    }
}
