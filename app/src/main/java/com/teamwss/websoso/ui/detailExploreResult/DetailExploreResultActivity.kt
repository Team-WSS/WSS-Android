package com.teamwss.websoso.ui.detailExploreResult

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityDetailExploreResultBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultAdapter
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Header
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Result
import com.teamwss.websoso.ui.detailExploreResult.model.DetailExploreResultUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreResultActivity :
    BindingActivity<ActivityDetailExploreResultBinding>(R.layout.activity_detail_explore_result) {
    private val detailExploreResultAdapter: DetailExploreResultAdapter by lazy {
        DetailExploreResultAdapter(::navigateToNovelDetail)
    }

    private val detailExploreResultViewModel: DetailExploreResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailExploreResultViewModel.updateSearchResult()
        setupTranslucentOnStatusBar()
        bindViewModel()
        setupAdapter()
        setupObserver()
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
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
                    0 -> 2
                    else -> 1
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

    private fun navigateToNovelDetail(novelId: Long) {
        // TODO 작품 상세로 이동
    }
}