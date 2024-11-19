package com.teamwss.websoso.ui.main.home

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.R.string.home_nickname_interest_feed
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.common.ui.model.ResultFrom.FeedDetailBack
import com.teamwss.websoso.common.ui.model.ResultFrom.FeedDetailRemoved
import com.teamwss.websoso.common.ui.model.ResultFrom.NormalExploreBack
import com.teamwss.websoso.common.ui.model.ResultFrom.NovelDetailBack
import com.teamwss.websoso.common.ui.model.ResultFrom.ProfileEditSuccess
import com.teamwss.websoso.databinding.FragmentHomeBinding
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.ui.main.MainViewModel
import com.teamwss.websoso.ui.main.home.adpater.PopularFeedsAdapter
import com.teamwss.websoso.ui.main.home.adpater.PopularNovelsAdapter
import com.teamwss.websoso.ui.main.home.adpater.RecommendedNovelsByUserTasteAdapter
import com.teamwss.websoso.ui.main.home.adpater.UserInterestFeedAdapter
import com.teamwss.websoso.ui.normalExplore.NormalExploreActivity
import com.teamwss.websoso.ui.notice.NoticeActivity
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
import com.teamwss.websoso.ui.profileEdit.ProfileEditActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val homeViewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

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

    private val startActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            FeedDetailBack.RESULT_OK, FeedDetailRemoved.RESULT_OK -> homeViewModel.updateFeed()
            NormalExploreBack.RESULT_OK, NovelDetailBack.RESULT_OK -> {
                homeViewModel.updateFeed()
                homeViewModel.updateNovel()
            }

            ProfileEditSuccess.RESULT_OK -> {
                mainViewModel.updateUserInfo()
                homeViewModel.updateNovel()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        setupAdapter()
        setupItemDecoration()
        setupObserver()
        setupDotsIndicator()
        onPostInterestNovelClick()
        onSettingPreferenceGenreClick()
        onNoticeButtonClick()
    }

    private fun bindViewModel() {
        binding.viewModel = homeViewModel
        binding.lifecycleOwner = this
    }

    private fun setupAdapter() {
        with(binding) {
            rvHomeTodayPopularNovel.adapter = popularNovelsAdapter
            vpHomePopularFeed.adapter = popularFeedsAdapter
            rvUserInterestFeed.adapter = userInterestFeedAdapter
            rvRecommendNovelByUserTaste.adapter = recommendedNovelsByUserTasteAdapter
        }
    }

    private fun setupItemDecoration() {
        with(binding) {
            rvHomeTodayPopularNovel.addItemDecoration(
                HomeCustomItemDecoration(
                    TODAY_POPULAR_NOVEL_MARGIN
                )
            )
            rvUserInterestFeed.addItemDecoration(HomeCustomItemDecoration(USER_INTEREST_MARGIN))
        }
    }

    private fun setupObserver() {
        mainViewModel.isLogin.observe(viewLifecycleOwner) { isLogin ->
            homeViewModel.updateHomeData(isLogin = isLogin)

            if (isLogin.not()) binding.tvHomeInterestFeed.text =
                getString(R.string.home_interest_feed_text)
        }

        mainViewModel.mainUiState.observe(viewLifecycleOwner) { uiState ->
            binding.tvHomeInterestFeed.text =
                getString(home_nickname_interest_feed, uiState.nickname)
        }

        homeViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.error -> {
                    binding.wllHome.setWebsosoLoadingVisibility(true)
                    binding.wllHome.setErrorLayoutVisibility(true)
                }

                !uiState.loading -> {
                    binding.wllHome.setWebsosoLoadingVisibility(false)
                    popularNovelsAdapter.submitList(uiState.popularNovels)
                    popularFeedsAdapter.submitList(uiState.popularFeeds)

                    mainViewModel.isLogin.value?.let { isLogin ->
                        if (isLogin) {
                            updateUserInterestFeedsVisibility(uiState.userInterestFeeds.isEmpty())
                            updateRecommendedNovelByUserTasteVisibility(uiState.recommendedNovelsByUserTaste.isEmpty())
                            userInterestFeedAdapter.submitList(uiState.userInterestFeeds)
                            recommendedNovelsByUserTasteAdapter.submitList(uiState.recommendedNovelsByUserTaste)
                        }
                    }
                }
            }
        }
    }

    private fun updateUserInterestFeedsVisibility(isUserInterestEmpty: Boolean) {
        with(binding) {
            if (isUserInterestEmpty) {
                when (homeViewModel.uiState.value?.isInterestNovel) {
                    true -> {
                        clHomeUserInterestFeed.visibility = View.GONE
                        clHomeInterestFeed.visibility = View.GONE
                        clHomeNoAssociatedFeed.visibility = View.VISIBLE
                    }

                    false -> {
                        clHomeUserInterestFeed.visibility = View.GONE
                        clHomeInterestFeed.visibility = View.VISIBLE
                        clHomeNoAssociatedFeed.visibility = View.GONE
                    }

                    else -> Unit
                }
            } else {
                clHomeUserInterestFeed.visibility = View.VISIBLE
                clHomeInterestFeed.visibility = View.GONE
                clHomeNoAssociatedFeed.visibility = View.GONE
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
        startActivityLauncher.launch(
            NovelDetailActivity.getIntent(
                requireContext(),
                novelId,
            )
        )
    }

    private fun navigateToFeedDetail(feedId: Long) {
        startActivityLauncher.launch(
            FeedDetailActivity.getIntent(
                requireContext(),
                feedId,
            )
        )
    }

    private fun onPostInterestNovelClick() {
        binding.clHomeInterestFeed.setOnClickListener {
            startActivityLauncher.launch(
                NormalExploreActivity.getIntent(
                    requireContext(),
                )
            )
        }
    }

    private fun onSettingPreferenceGenreClick() {
        binding.clHomeRecommendNovel.setOnClickListener {
            startActivityLauncher.launch(
                ProfileEditActivity.getIntent(
                    requireContext(),
                )
            )
        }
    }

    private fun onNoticeButtonClick() {
        binding.ivHomeNotification.setOnClickListener {
            startActivity(NoticeActivity.getIntent(requireContext()))
        }
    }

    companion object {
        private const val TODAY_POPULAR_NOVEL_MARGIN = 15
        private const val USER_INTEREST_MARGIN = 14
    }
}
