package com.teamwss.websoso.ui.normalExplore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNormalExploreBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NormalExploreActivity :
    BindingActivity<ActivityNormalExploreBinding>(R.layout.activity_normal_explore) {
    private val normalExploreAdapter: NormalExploreAdapter by lazy { NormalExploreAdapter(::navigateToNovelDetail) }
    private val normalExploreViewModel: NormalExploreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupUI()
        setObserveUiState()
    }

    private fun bindViewModel() {
        binding.normalExploreViewModel = normalExploreViewModel
        binding.onClick = onClickNormalExploreButton()
        binding.lifecycleOwner = this
    }

    private fun onClickNormalExploreButton() = object : NormalExploreClickListener {

        override fun onClickBackButton() {
            finish()
        }

        override fun onClickSearchButton() {
            normalExploreViewModel.fetchNormalExploreResult()
        }

        override fun onClickSearchWordCancelButton() {
            binding.etNormalExploreSearchContent.text.clear()
        }
    }

    private fun setupUI() {
        setupTranslucentOnStatusBar()
        setupFocusSearchBar()
        setupNormalExploreAdapter()
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun setupFocusSearchBar() {
        binding.etNormalExploreSearchContent.requestFocus()
    }

    private fun setupNormalExploreAdapter() {
        binding.rvNormalExploreResult.adapter = normalExploreAdapter
        binding.rvNormalExploreResult.setHasFixedSize(true)
    }

    private fun navigateToNovelDetail(novelId: Long) {
        // TODO 작품 정보 뷰로 이동
    }

    private fun setObserveUiState() {
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

    companion object {

        fun from(
            context: Context,
        ): Intent = Intent(context, NormalExploreActivity::class.java)
    }
}