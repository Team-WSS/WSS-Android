package com.into.websoso.ui.detailExploreResult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.into.websoso.R
import com.into.websoso.common.ui.base.BaseActivity
import com.into.websoso.common.util.InfiniteScrollListener
import com.into.websoso.common.util.SingleEventHandler
import com.into.websoso.common.util.getAdaptedParcelableExtra
import com.into.websoso.databinding.ActivityDetailExploreResultBinding
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultAdapter
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Header
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.ItemType.HEADER
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.ItemType.LOADING
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.ItemType.NOVELS
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Loading
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Novels
import com.into.websoso.ui.detailExploreResult.model.DetailExploreFilteredModel
import com.into.websoso.ui.detailExploreResult.model.DetailExploreResultUiState
import com.into.websoso.ui.novelDetail.NovelDetailActivity
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
                return when (detailExploreResultAdapter.getItemViewType(position)) {
                    HEADER.ordinal -> FULL_SPAN
                    NOVELS.ordinal -> HALF_SPAN
                    LOADING.ordinal -> FULL_SPAN
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
                    binding.wllDetailExploreResult.setWebsosoLoadingVisibility(true)
                    binding.wllDetailExploreResult.setErrorLayoutVisibility(false)
                }

                uiState.error -> {
                    binding.wllDetailExploreResult.setWebsosoLoadingVisibility(false)
                    binding.wllDetailExploreResult.setErrorLayoutVisibility(true)
                }

                else -> {
                    binding.wllDetailExploreResult.setWebsosoLoadingVisibility(false)
                    binding.wllDetailExploreResult.setErrorLayoutVisibility(false)
                    updateView(uiState)
                }
            }
        }

        detailExploreResultViewModel.appliedFiltersMessage.observe(this) { filteredMessage ->
            when (filteredMessage?.isNotEmpty()) {
                true -> {
                    binding.tvDetailExploreResultFilter.text = filteredMessage
                    binding.tvDetailExploreResultFilterDescription.isVisible = true
                }

                false -> {
                    binding.tvDetailExploreResultFilter.text = filteredMessage
                    binding.tvDetailExploreResultFilterDescription.isVisible = false
                }

                else -> return@observe
            }
        }
    }

    private fun updateView(uiState: DetailExploreResultUiState) {
        val header = Header(uiState.novelCount)
        val novels = uiState.novels.map { Novels(it) }

        if (uiState.novels.isNotEmpty()) {
            when (uiState.isLoadable) {
                true -> detailExploreResultAdapter.submitList(listOf(header) + novels + Loading)
                false -> detailExploreResultAdapter.submitList(listOf(header) + novels)
            }
        }
    }

    private fun onBackButtonClick() {
        binding.ivDetailExploreResultBackButton.setOnClickListener {
            finish()
        }
    }

    private fun onEditFilterItemButtonClick() {
        binding.clDetailExploreResultFilterButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                val detailExploreResultDialogBottomSheet =
                    DetailExploreResultDialogBottomSheet.newInstance()
                detailExploreResultDialogBottomSheet.show(
                    supportFragmentManager,
                    DETAIL_EXPLORE_RESULT_BOTTOM_SHEET_TAG,
                )

                detailExploreResultViewModel.updateIsBottomSheetOpen(true)
            }
        }
    }

    private fun navigateToNovelDetail(novelId: Long) {
        val intent = NovelDetailActivity.getIntent(this, novelId)
        startActivity(intent)
    }

    companion object {
        private const val FULL_SPAN = 2
        private const val HALF_SPAN = 1
        const val DETAIL_EXPLORE_RESULT_BOTTOM_SHEET_TAG =
            "DetailExploreResultDialogBottomSheet"
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
