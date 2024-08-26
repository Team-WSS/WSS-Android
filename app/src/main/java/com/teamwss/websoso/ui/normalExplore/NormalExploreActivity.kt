package com.teamwss.websoso.ui.normalExplore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNormalExploreBinding
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreAdapter
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreItemType.Header
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreItemType.Result
import com.teamwss.websoso.ui.normalExplore.model.NormalExploreUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NormalExploreActivity :
    BaseActivity<ActivityNormalExploreBinding>(R.layout.activity_normal_explore) {
    private val normalExploreAdapter: NormalExploreAdapter by lazy { NormalExploreAdapter(::navigateToNovelDetail) }
    private val normalExploreViewModel: NormalExploreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupUI()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.normalExploreViewModel = normalExploreViewModel
        binding.lifecycleOwner = this
    }

    private fun setupUI() {
        binding.apply {
            etNormalExploreSearchContent.requestFocus()
            rvNormalExploreResult.adapter = normalExploreAdapter
            onClick = onNormalExploreButtonClick()
        }
    }

    private fun onNormalExploreButtonClick() = object : NormalExploreClickListener {

        override fun onBackButtonClick() {
            finish()
        }

        override fun onSearchButtonClick() {
            normalExploreViewModel.updateSearchResult()
        }

        override fun onSearchCancelButtonClick() {
            normalExploreViewModel.updateSearchWordEmpty()
        }

        override fun onNovelInquireButtonClick() {
            // TODO 카카오톡 채널로 연결
        }
    }

    private fun navigateToNovelDetail(novelId: Long) {
        // TODO 작품 정보 뷰로 이동
    }

    private fun setupObserver() {
        normalExploreViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.loading -> {
                    binding.wlNormalExplore.setWebsosoLoadingVisibility(true)
                    binding.wlNormalExplore.setErrorLayoutVisibility(false)
                }

                uiState.error -> {
                    binding.wlNormalExplore.setWebsosoLoadingVisibility(false)
                    binding.wlNormalExplore.setErrorLayoutVisibility(true)
                }

                else -> {
                    binding.wlNormalExplore.setWebsosoLoadingVisibility(false)
                    binding.wlNormalExplore.setErrorLayoutVisibility(false)
                    updateView(uiState)
                }
            }
        }

        normalExploreViewModel.searchWord.observe(this) {
            normalExploreViewModel.validateSearchWordClearButton()
        }
    }

    private fun updateView(uiState: NormalExploreUiState) {
        val header = Header(uiState.novels.count())
        val results = uiState.novels.map { Result(it) }
        normalExploreAdapter.submitList(listOf(header) + results)
    }

    companion object {

        fun getIntent(context: Context): Intent =
            Intent(context, NormalExploreActivity::class.java)
    }
}