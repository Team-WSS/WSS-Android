package com.teamwss.websoso.ui.main.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentHomeBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.main.home.adpater.PopularNovelsAdapter
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
import com.teamwss.websoso.util.toIntScaledByPx
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BindingFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val homeViewModel: HomeViewModel by viewModels()

    private val popularNovelsAdapter: PopularNovelsAdapter by lazy {
        PopularNovelsAdapter(::navigateToNovelDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupViewPager()
        setupObserver()
    }

    private fun setupAdapter() {
        binding.vpHomeTodayPopularNovel.adapter = popularNovelsAdapter
    }

    private fun setupViewPager() {
        val recyclerView = binding.vpHomeTodayPopularNovel.getChildAt(0) as RecyclerView

        val paddingPx = TODAY_POPULAR_NOVEL_PADDING.toIntScaledByPx()
        recyclerView.apply {
            setPadding(paddingPx, 0, paddingPx, 0)
            clipToPadding = false
        }

        binding.vpHomeTodayPopularNovel.setPageTransformer(
            MarginPageTransformer(
                TODAY_POPULAR_NOVEL_MARGIN
            )
        )
    }

    private fun setupObserver() {
        homeViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.loading -> Unit
                uiState.error -> Unit
                !uiState.loading -> {
                    popularNovelsAdapter.submitList(uiState.popularNovels)
                }
            }
        }
    }

    private fun navigateToNovelDetail(novelId: Long) {
        startActivity(NovelDetailActivity.getIntent(requireContext(), novelId))
    }

    companion object {
        private const val TODAY_POPULAR_NOVEL_PADDING = 20
        private const val TODAY_POPULAR_NOVEL_MARGIN = 10
    }
}
