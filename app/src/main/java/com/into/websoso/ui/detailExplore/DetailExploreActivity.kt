package com.into.websoso.ui.detailExplore

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.into.websoso.core.common.util.setupSystemBarIconColor
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.string.inquire_link
import com.into.websoso.ui.detailExploreResult.DetailExploreResultActivity
import com.into.websoso.ui.detailExploreResult.model.DetailExploreFilteredModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreActivity : AppCompatActivity() {
    private val detailExploreViewModel: DetailExploreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupSystemBarIconColor(true)

        setContent {
            WebsosoTheme {
                DetailExploreScreen(
                    viewModel = detailExploreViewModel,
                    onBackClick = ::finish,
                    onSearchClick = ::navigateToSearchResult,
                    onKeywordInquireClick = ::navigateToKeywordInquire,
                )
            }
        }
    }

    private fun navigateToSearchResult() {
        val selectedGenres = detailExploreViewModel.selectedGenres.value ?: emptyList()
        val isCompleted = detailExploreViewModel.selectedStatus.value?.isCompleted
        val novelRating = detailExploreViewModel.selectedRating.value

        val keywordIds = detailExploreViewModel.uiState.value
            ?.categories
            ?.flatMap { it.keywords.asSequence() }
            ?.filter { it.isSelected }
            ?.map { it.keywordId }
            ?.toList() ?: emptyList()

        startActivity(
            DetailExploreResultActivity.getIntent(
                context = this,
                detailExploreFilteredModel = DetailExploreFilteredModel(
                    genres = selectedGenres,
                    isCompleted = isCompleted,
                    novelRating = novelRating,
                    keywordIds = keywordIds,
                ),
            ),
        )
    }

    private fun navigateToKeywordInquire() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(inquire_link)))
        startActivity(intent)
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, DetailExploreActivity::class.java)
    }
}