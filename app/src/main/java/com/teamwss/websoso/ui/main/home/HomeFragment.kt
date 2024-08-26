package com.teamwss.websoso.ui.main.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.common.util.toIntScaledByPx
import com.teamwss.websoso.databinding.FragmentHomeBinding
import com.teamwss.websoso.ui.common.dialog.LoginRequestDialogFragment
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.ui.main.home.adpater.PopularFeedsAdapter
import com.teamwss.websoso.ui.main.home.adpater.PopularNovelsAdapter
import com.teamwss.websoso.ui.normalExplore.NormalExploreActivity
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val homeViewModel: HomeViewModel by viewModels()

    private val popularNovelsAdapter: PopularNovelsAdapter by lazy {
        PopularNovelsAdapter(::navigateToNovelDetail)
    }

    private val popularFeedsAdapter: PopularFeedsAdapter by lazy {
        PopularFeedsAdapter(::navigateToFeedDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        setupAdapter()
        setupViewPager()
        setupObserver()
        setupDotsIndicator()
        onPostInterestNovelClick()
        onSettingInterestClick()
    }

    private fun bindViewModel() {
        binding.viewModel = homeViewModel
        binding.lifecycleOwner = this
    }

    private fun setupAdapter() {
        binding.vpHomeTodayPopularNovel.adapter = popularNovelsAdapter
        binding.vpHomePopularFeed.adapter = popularFeedsAdapter
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
            updateViewVisibleByLogin(uiState.isLogin, uiState.nickname)
            when {
                uiState.loading -> Unit
                uiState.error -> Unit
                !uiState.loading -> {
                    popularNovelsAdapter.submitList(uiState.popularNovels)
                    popularFeedsAdapter.submitList(uiState.popularFeeds)
                }
            }
        }
    }

    private fun updateViewVisibleByLogin(isLogin: Boolean, nickname: String?) {
        with(binding) {
            when (isLogin) {
                true -> {
                    tvHomeInterestFeed.text = "$nickname 님의 관심글"
                    clHomeInterestFeed.visibility = View.GONE
                    clHomeRecommendNovel.visibility = View.GONE
                    clHomeUserInterestFeed.visibility = View.VISIBLE
                    clHomeUserRecommendNovel.visibility = View.VISIBLE
                }

                false -> {
                    tvHomeInterestFeed.text = "관심글"
                    clHomeInterestFeed.visibility = View.VISIBLE
                    clHomeRecommendNovel.visibility = View.VISIBLE
                    clHomeUserInterestFeed.visibility = View.GONE
                    clHomeUserRecommendNovel.visibility = View.GONE
                }
            }
        }
    }

    private fun setupDotsIndicator() {
        binding.dotsIndicatorHome.attachTo(binding.vpHomePopularFeed)
    }

    private fun navigateToNovelDetail(novelId: Long) {
        startActivity(NovelDetailActivity.getIntent(requireContext(), novelId))
    }

    private fun navigateToFeedDetail(feedId: Long) {
        startActivity(FeedDetailActivity.getIntent(requireContext(), feedId))
    }

    private fun onPostInterestNovelClick() {
        binding.clHomeInterestFeed.setOnClickListener {
            if (homeViewModel.uiState.value?.isLogin == true) {
                startActivity(NormalExploreActivity.getIntent(requireContext()))
            } else {
                showLoginRequestDialog()
            }
        }
    }

    private fun onSettingInterestClick() {
        binding.clHomeRecommendNovel.setOnClickListener {
            if (homeViewModel.uiState.value?.isLogin == true) {
                //TODO 프로필 수정으로 이동
            } else {
                showLoginRequestDialog()
            }
        }
    }

    private fun showLoginRequestDialog() {
        val dialog = LoginRequestDialogFragment.newInstance()
        dialog.show(parentFragmentManager, LoginRequestDialogFragment.TAG)
    }

    companion object {
        private const val TODAY_POPULAR_NOVEL_PADDING = 20
        private const val TODAY_POPULAR_NOVEL_MARGIN = 10
    }
}
