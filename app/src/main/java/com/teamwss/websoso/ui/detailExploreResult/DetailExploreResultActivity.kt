package com.teamwss.websoso.ui.detailExploreResult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityDetailExploreResultBinding
import com.teamwss.websoso.ui.detailExplore.DetailExploreDialogBottomSheet
import com.teamwss.websoso.ui.detailExplore.info.model.Genre
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultAdapter
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Header
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Result
import com.teamwss.websoso.ui.detailExploreResult.model.DetailExploreResultUiState
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreResultActivity :
    BaseActivity<ActivityDetailExploreResultBinding>(R.layout.activity_detail_explore_result) {
    private val detailExploreResultAdapter: DetailExploreResultAdapter by lazy {
        DetailExploreResultAdapter(::navigateToNovelDetail)
    }

    private val detailExploreResultViewModel: DetailExploreResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val genreNames = intent.getStringArrayListExtra("GENRES") ?: arrayListOf()
        val isCompleted = intent.getBooleanExtra("IS_COMPLETED", false)
        val novelRating = intent.getFloatExtra("NOVEL_RATING", 0f)
        val keywordIds = intent.getIntegerArrayListExtra("KEYWORD_IDS") ?: arrayListOf()

        detailExploreResultViewModel.updateSearchResult()
        bindViewModel()
        setupAdapter()
        setupObserver()
        onBackButtonClick()
        onEditFilterItemButtonClick()
    }

    private fun bindViewModel() {
        binding.detailExploreResultViewModel = detailExploreResultViewModel
        binding.lifecycleOwner = this
    }

    private fun setupAdapter() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    HEADER_POSITION -> FULL_SPAN
                    else -> HALF_SPAN
                }
            }
        }
        binding.apply {
            rvDetailExploreResult.layoutManager = gridLayoutManager
            rvDetailExploreResult.adapter = detailExploreResultAdapter
        }
    }

    private fun setupObserver() {
        detailExploreResultViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.loading -> {
                    binding.wlDetailExploreResult.setWebsosoLoadingVisibility(true)
                    binding.wlDetailExploreResult.setErrorLayoutVisibility(false)
                }

                uiState.error -> {
                    binding.wlDetailExploreResult.setWebsosoLoadingVisibility(false)
                    binding.wlDetailExploreResult.setErrorLayoutVisibility(true)
                }

                else -> {
                    binding.wlDetailExploreResult.setWebsosoLoadingVisibility(false)
                    binding.wlDetailExploreResult.setErrorLayoutVisibility(false)
                    updateView(uiState)
                }
            }
        }
    }

    private fun updateView(uiState: DetailExploreResultUiState) {
        val header = Header(uiState.novels.count())
        val results = uiState.novels.map { Result(it) }
        detailExploreResultAdapter.submitList(listOf(header) + results)
    }

    private fun onBackButtonClick() {
        binding.ivDetailExploreResultBackButton.setOnClickListener {
            finish()
        }
    }

    private fun onEditFilterItemButtonClick() {
        binding.clDetailExploreResultFilterButton.setOnClickListener {
            val detailExploreBottomSheet = DetailExploreDialogBottomSheet.newInstance()
            detailExploreBottomSheet.show(supportFragmentManager, DETAIL_EXPLORE_BOTTOM_SHEET_TAG)
        }
    }

    private fun navigateToNovelDetail(novelId: Long) {
        val intent = NovelDetailActivity.getIntent(this, novelId)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val HEADER_POSITION = 0
        private const val FULL_SPAN = 2
        private const val HALF_SPAN = 1
        private const val DETAIL_EXPLORE_BOTTOM_SHEET_TAG = "DetailExploreDialogBottomSheet"
        private const val GENRES_KEY = "GENRES"
        private const val IS_COMPLETED_KEY = "IS_COMPLETED"
        private const val NOVEL_RATING_KEY = "NOVEL_RATING"
        private const val KEYWORD_IDS_KEY = "KEYWORD_IDS"

        fun getIntent(
            context: Context,
            genres: List<Genre>?,
            isCompleted: Boolean?,
            novelRating: Float?,
            keywordIds: List<Int>,
        ): Intent {
            return Intent(context, DetailExploreResultActivity::class.java).apply {
                putStringArrayListExtra(GENRES_KEY, genres?.map { it.name }?.let { ArrayList(it) })
                putExtra(IS_COMPLETED_KEY, isCompleted)
                putExtra(NOVEL_RATING_KEY, novelRating)
                putIntegerArrayListExtra(KEYWORD_IDS_KEY, ArrayList(keywordIds))
            }
        }
    }
}
