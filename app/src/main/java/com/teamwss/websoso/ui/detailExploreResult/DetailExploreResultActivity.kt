package com.teamwss.websoso.ui.detailExploreResult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.util.InfiniteScrollListener
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.common.util.getAdaptedParcelableExtra
import com.teamwss.websoso.databinding.ActivityDetailExploreResultBinding
import com.teamwss.websoso.ui.detailExplore.DetailExploreDialogBottomSheet
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultAdapter
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Header
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Novels
import com.teamwss.websoso.ui.detailExploreResult.model.DetailExploreFilteredModel
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
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initDetailExploreFilteredValue()
        bindViewModel()
        setupAdapter()
        setupObserver()
        onBackButtonClick()
        onEditFilterItemButtonClick()
    }

    private fun initDetailExploreFilteredValue() {
        val detailExploreFilteredValue =
            intent.getAdaptedParcelableExtra<DetailExploreFilteredModel>(
                DETAIL_EXPLORE_FILTERED_INFO,
            )

        detailExploreFilteredValue?.let {
            detailExploreResultViewModel.updatePreviousSearchFilteredValue(
                it,
            )
        }
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
        binding.rvDetailExploreResult.apply {
            layoutManager = gridLayoutManager
            adapter = detailExploreResultAdapter
            addOnScrollListener(
                InfiniteScrollListener.of(
                    singleEventHandler = singleEventHandler,
                    event = { detailExploreResultViewModel.updateSearchResult(false) },
                )
            )
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
        val header = Header(uiState.novelCount)
        val novels = uiState.novels.map { Novels(it) }
        detailExploreResultAdapter.submitList(listOf(header) + novels)
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
        private const val DETAIL_EXPLORE_FILTERED_INFO = "DetailExploreFilteredInfo"

        fun getIntent(
            context: Context,
            detailExploreFilteredModel: DetailExploreFilteredModel,
        ): Intent {
            return Intent(context, DetailExploreResultActivity::class.java).apply {
                putExtra(DETAIL_EXPLORE_FILTERED_INFO, detailExploreFilteredModel)
            }
        }
    }
}
