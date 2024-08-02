package com.teamwss.websoso.ui.normalExplore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNormalExploreBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreAdapter
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreCountAdapter
import com.teamwss.websoso.ui.normalExplore.model.NormalExploreUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NormalExploreActivity :
    BindingActivity<ActivityNormalExploreBinding>(R.layout.activity_normal_explore) {
    private val normalExploreAdapter: NormalExploreAdapter by lazy { NormalExploreAdapter(::navigateToNovelDetail) }
    private val normalExploreCountAdapter: NormalExploreCountAdapter by lazy { NormalExploreCountAdapter() }
    private val combinedScrollAreaAdapter: ConcatAdapter by lazy {
        ConcatAdapter(
            normalExploreCountAdapter,
            normalExploreAdapter,
        )
    }

    private val normalExploreViewModel: NormalExploreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupUI()
        setupUiStateObserver()
        setupSearchWordObserver()
    }

    private fun bindViewModel() {
        binding.normalExploreViewModel = normalExploreViewModel
        binding.lifecycleOwner = this
    }

    private fun setupUI() {
        binding.apply {
            etNormalExploreSearchContent.requestFocus()
            rvNormalExploreResult.adapter = combinedScrollAreaAdapter
            onClick = onNormalExploreButtonClick()
        }
        setupTranslucentOnStatusBar()
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

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun navigateToNovelDetail(novelId: Long) {
        // TODO 작품 정보 뷰로 이동
    }

    private fun setupUiStateObserver() {
        normalExploreViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.loading -> loading()
                uiState.error -> throw IllegalStateException()
                !uiState.loading -> updateResultView(uiState)
            }
        }
    }

    private fun loading() {
        // TODO 로딩 뷰
    }

    private fun updateResultView(uiState: NormalExploreUiState) {
        normalExploreAdapter.updateResultNovels(uiState.novels)
        normalExploreCountAdapter.updateResultNovels(uiState.novels.count())
    }

    private fun setupSearchWordObserver() {
        normalExploreViewModel.searchWord.observe(this) {
            normalExploreViewModel.validateSearchWordClearButton()
        }
    }

    companion object {

        fun from(
            context: Context,
        ): Intent = Intent(context, NormalExploreActivity::class.java)
    }
}