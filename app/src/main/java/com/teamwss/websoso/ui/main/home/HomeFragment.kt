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
import com.teamwss.websoso.ui.main.home.adpater.RecommendedNovelsByUserTasteAdapter
import com.teamwss.websoso.ui.main.home.adpater.UserInterestFeedAdapter
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

    private val userInterestFeedAdapter: UserInterestFeedAdapter by lazy {
        UserInterestFeedAdapter(::navigateToNovelDetail)
    }

    private val recommendedNovelsByUserTasteAdapter: RecommendedNovelsByUserTasteAdapter by lazy {
        RecommendedNovelsByUserTasteAdapter(::navigateToNovelDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        setupAdapter()
        setupUserInterestViewPager()
        setupPopularNovelViewPager()
        setupObserver()
        setupDotsIndicator()
        onPostInterestNovelClick()
        onSettingPreferenceGenreClick()
    }

    private fun bindViewModel() {
        binding.viewModel = homeViewModel
        binding.lifecycleOwner = this
    }

    private fun setupAdapter() {
        with(binding) {
            vpHomeTodayPopularNovel.adapter = popularNovelsAdapter
            vpHomePopularFeed.adapter = popularFeedsAdapter
            vpUserInterestFeed.adapter = userInterestFeedAdapter
            rvRecommendNovelByUserTaste.adapter = recommendedNovelsByUserTasteAdapter
        }
    }

    private fun setupPopularNovelViewPager() {
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

    private fun setupUserInterestViewPager() {
        val recyclerView = binding.vpUserInterestFeed.getChildAt(0) as RecyclerView

        val paddingPx = USER_INTEREST_PADDING.toIntScaledByPx()
        recyclerView.apply {
            setPadding(paddingPx, 0, paddingPx, 0)
            clipToPadding = false
        }

        binding.vpUserInterestFeed.setPageTransformer(
            MarginPageTransformer(
                USER_INTEREST_MARGIN
            )
        )
    }

    private fun setupObserver() {
        homeViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            updateViewVisibilityByLogin(uiState.isLogin, uiState.nickname)
            if (uiState.isLogin) {
                updateUserInterestFeedsVisibility(uiState.userInterestFeeds.isEmpty())
                updateRecommendedNovelByUserTasteVisibility(uiState.recommendedNovelsByUserTaste.isEmpty())
            }
            when {
                uiState.loading -> Unit
                uiState.error -> Unit
                !uiState.loading -> {
                    popularNovelsAdapter.submitList(uiState.popularNovels)
                    popularFeedsAdapter.submitList(uiState.popularFeeds)
                    userInterestFeedAdapter.submitList(uiState.userInterestFeeds)
                    recommendedNovelsByUserTasteAdapter.submitList(uiState.recommendedNovelsByUserTaste)
                }
            }
        }
    }

    private fun updateViewVisibilityByLogin(isLogin: Boolean, nickname: String?) {
        with(binding) {
            when (isLogin) {
                true -> {
                    tvHomeInterestFeed.text =
                        getString(R.string.home_nickname_interest_feed, nickname)
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

    private fun updateUserInterestFeedsVisibility(isUserInterestEmpty: Boolean) {
        with(binding) {
            if (isUserInterestEmpty) {
                clHomeUserInterestFeed.visibility = View.GONE
                clHomeInterestFeed.visibility = View.VISIBLE
            } else {
                clHomeUserInterestFeed.visibility = View.VISIBLE
                clHomeInterestFeed.visibility = View.GONE
            }
        }
    }

    private fun updateRecommendedNovelByUserTasteVisibility(isRecommendNovelByUserTasteEmpty: Boolean) {
        with(binding) {
            if (isRecommendNovelByUserTasteEmpty) {
                clHomeUserRecommendNovel.visibility = View.GONE
                clHomeRecommendNovel.visibility = View.VISIBLE
            } else {
                clHomeUserRecommendNovel.visibility = View.VISIBLE
                clHomeRecommendNovel.visibility = View.GONE
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

    private fun onSettingPreferenceGenreClick() {
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
        private const val TODAY_POPULAR_NOVEL_MARGIN = 20

        private const val USER_INTEREST_PADDING = 20
        private const val USER_INTEREST_MARGIN = 20
    }
}
