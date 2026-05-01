package com.into.websoso.ui.detailExplore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.string.detail_explore_info_genre
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
import com.into.websoso.ui.detailExplore.info.model.SeriesStatus

@Composable
fun DetailExploreInfoTab(
    viewModel: DetailExploreViewModel,
    modifier: Modifier = Modifier,
) {
    val selectedGenres by viewModel.selectedGenres.observeAsState(emptyList())
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
        Spacer(modifier = Modifier.height(8.dp))
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
                )
            }
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
        modifier = modifier.wrapContentHeight(),
    )
}

@Composable
private fun RatingSection(
    min: Float,
    max: Float,
    onRangeChange: (Float, Float) -> Unit,
) {
    Column {
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
        Spacer(modifier = Modifier.height(6.dp))
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
