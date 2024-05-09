package com.teamwss.websoso.ui.main.explore.normalExplore

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNormalExploreBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.main.explore.normalExplore.adapter.NormalExploreAdapter

class NormalExploreActivity :
    BindingActivity<ActivityNormalExploreBinding>(R.layout.activity_normal_explore) {
    private val normalExploreAdapter: NormalExploreAdapter by lazy { NormalExploreAdapter(::navigateToNovelDetail) }
    private val normalExploreViewModel: NormalExploreViewModel by viewModels { NormalExploreViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_explore)

        setupUI()
        observeUiState()
    }

    private fun setupUI() {
        setTranslucentOnStatusBar()
        setFocusSearchBar()
        initNormalExploreAdapter()
    }

    private fun setTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun setFocusSearchBar() {
        binding.etNormalExploreSearchContent.requestFocus()
    }

    private fun initNormalExploreAdapter() {
        binding.rvNormalExploreResult.adapter = normalExploreAdapter
        binding.rvNormalExploreResult.setHasFixedSize(true)
    }

    private fun navigateToNovelDetail(novelId: Long) {
        // TODO 작품 정보 뷰로 이동
    }

    private fun observeUiState() {
        normalExploreViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.loading -> loading()
                uiState.error -> throw IllegalStateException()
                !uiState.loading -> normalExploreAdapter.submitList(uiState.novels)
            }
        }
    }

    private fun loading() {
        // TODO 로딩 뷰
    }
}