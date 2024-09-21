package com.teamwss.websoso.ui.main.home

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.databinding.FragmentHomeBinding
import com.teamwss.websoso.ui.common.dialog.LoginRequestDialogFragment
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.ui.main.MainViewModel
import com.teamwss.websoso.ui.main.home.adpater.PopularFeedsAdapter
import com.teamwss.websoso.ui.main.home.adpater.PopularNovelsAdapter
import com.teamwss.websoso.ui.main.home.adpater.RecommendedNovelsByUserTasteAdapter
import com.teamwss.websoso.ui.main.home.adpater.UserInterestFeedAdapter
import com.teamwss.websoso.ui.normalExplore.NormalExploreActivity
import com.teamwss.websoso.ui.notice.NoticeActivity
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
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
        if (result.resultCode == Activity.RESULT_OK) {
            homeViewModel.updateHomeData()
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
        homeViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.loading -> Unit
                uiState.error -> Unit
                !uiState.loading -> {
                    popularNovelsAdapter.submitList(uiState.popularNovels)
                    popularFeedsAdapter.submitList(uiState.popularFeeds)

                    mainViewModel.mainUiState.value?.let { mainUiState ->
                        if (mainUiState.isLogin) {
                            updateUserInterestFeedsVisibility(uiState.userInterestFeeds.isEmpty())
                            updateRecommendedNovelByUserTasteVisibility(uiState.recommendedNovelsByUserTaste.isEmpty())
                            userInterestFeedAdapter.submitList(uiState.userInterestFeeds)
                            recommendedNovelsByUserTasteAdapter.submitList(uiState.recommendedNovelsByUserTaste)
                        }
                        updateViewVisibilityByLogin(mainUiState.isLogin, uiState.nickname)
                    }
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
                    clHomeRecommendNovel.visibility = View.GONE
                    clHomeUserRecommendNovel.visibility = View.VISIBLE
                }

                false -> {
                    tvHomeInterestFeed.text = "관심글"
                    clHomeRecommendNovel.visibility = View.VISIBLE
                    clHomeUserRecommendNovel.visibility = View.GONE
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
                SOURCE_HOME
            )
        )
    }

    private fun navigateToFeedDetail(feedId: Long) {
        startActivityLauncher.launch(
            FeedDetailActivity.getIntent(
                requireContext(),
                feedId,
                SOURCE_HOME
            )
        )
    }

    private fun onPostInterestNovelClick() {
        binding.clHomeInterestFeed.setOnClickListener {
            if (mainViewModel.mainUiState.value?.isLogin == true) {
                startActivityLauncher.launch(
                    NormalExploreActivity.getIntent(
                        requireContext(),
                        SOURCE_HOME
                    )
                )
            } else {
                showLoginRequestDialog()
            }
        }
    }

    private fun onSettingPreferenceGenreClick() {
        binding.clHomeRecommendNovel.setOnClickListener {
            if (mainViewModel.mainUiState.value?.isLogin == true) {
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

    private fun onNoticeButtonClick() {
        binding.ivHomeNotification.setOnClickListener {
            if (mainViewModel.mainUiState.value?.isLogin == true) {
                startActivity(NoticeActivity.getIntent(requireContext()))
            } else {
                showLoginRequestDialog()
            }
        }
    }

    companion object {
        private const val TODAY_POPULAR_NOVEL_MARGIN = 15
        private const val USER_INTEREST_MARGIN = 14

        const val SOURCE_HOME = "Home"
    }
}
