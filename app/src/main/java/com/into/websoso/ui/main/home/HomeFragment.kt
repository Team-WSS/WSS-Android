package com.into.websoso.ui.main.home

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.firebase.messaging.FirebaseMessaging
import com.into.websoso.R
import com.into.websoso.R.string.home_nickname_interest_feed
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.ui.model.ResultFrom.FeedDetailBack
import com.into.websoso.core.common.ui.model.ResultFrom.FeedDetailRemoved
import com.into.websoso.core.common.ui.model.ResultFrom.NormalExploreBack
import com.into.websoso.core.common.ui.model.ResultFrom.Notification
import com.into.websoso.core.common.ui.model.ResultFrom.NovelDetailBack
import com.into.websoso.core.common.ui.model.ResultFrom.ProfileEditSuccess
import com.into.websoso.core.common.util.collectWithLifecycle
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.databinding.FragmentHomeBinding
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.main.MainViewModel
import com.into.websoso.ui.main.home.adpater.PopularFeedsAdapter
import com.into.websoso.ui.main.home.adpater.PopularNovelsAdapter
import com.into.websoso.ui.main.home.adpater.RecommendedNovelsByUserTasteAdapter
import com.into.websoso.ui.main.home.adpater.UserInterestFeedAdapter
import com.into.websoso.ui.main.home.dialog.TermsAgreementDialogFragment
import com.into.websoso.ui.normalExplore.NormalExploreActivity
import com.into.websoso.ui.notification.NotificationActivity
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.into.websoso.ui.profileEdit.ProfileEditActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    @Inject
    lateinit var tracker: Tracker

    private val homeViewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val homeResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Notification.RESULT_OK -> {
                    homeViewModel.updateNotificationUnread()
                }
            }
        }

    private val popularNovelsAdapter: PopularNovelsAdapter by lazy {
        PopularNovelsAdapter(::onPopularNovelClick)
    }

    private val popularFeedsAdapter: PopularFeedsAdapter by lazy {
        PopularFeedsAdapter(::onPopularFeedClick)
    }

    private val userInterestFeedAdapter: UserInterestFeedAdapter by lazy {
        UserInterestFeedAdapter(::onUserInterestNovelFeedClick)
    }

    private val recommendedNovelsByUserTasteAdapter: RecommendedNovelsByUserTasteAdapter by lazy {
        RecommendedNovelsByUserTasteAdapter(::onRecommendedNovelClick)
    }

    private val startActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            updateFCMToken(
                isFirstLaunch = true,
            )
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        setupAdapter()
        setupItemDecoration()
        setupObserver()
        setupDotsIndicator()
        onPostInterestNovelClick()
        onSettingPreferenceGenreClick()
        onNotificationButtonClick()
        tracker.trackEvent("home")
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
                    TODAY_POPULAR_NOVEL_MARGIN,
                ),
            )
            rvUserInterestFeed.addItemDecoration(HomeCustomItemDecoration(USER_INTEREST_MARGIN))
        }
    }

    private fun setupObserver() {
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
                    updateUserInterestFeedsVisibility(uiState.userInterestFeeds.isEmpty())
                    updateRecommendedNovelByUserTasteVisibility(uiState.recommendedNovelsByUserTaste.isEmpty())
                    userInterestFeedAdapter.submitList(uiState.userInterestFeeds)
                    recommendedNovelsByUserTasteAdapter.submitList(uiState.recommendedNovelsByUserTaste)
                    updateHasNotificationUnread(uiState.isNotificationUnread)
                }
            }
        }

        homeViewModel.isNotificationPermissionFirstLaunched.observe(viewLifecycleOwner) { isFirstLaunch ->
            if (isFirstLaunch) {
                showNotificationPermissionDialog()
                return@observe
            }
            updateFCMToken(isFirstLaunch = false)
        }

        homeViewModel.showTermsAgreementDialog.collectWithLifecycle(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                showTermsAgreementDialog()
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

    private fun updateHasNotificationUnread(hasUnread: Boolean) {
        binding.ivHomeNotification.isSelected = hasUnread
    }

    private fun showNotificationPermissionDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            homeViewModel.updateIsNotificationPermissionFirstLaunched(false)
            return
        }
        updateFCMToken(isFirstLaunch = true)
        homeViewModel.updateIsNotificationPermissionFirstLaunched(false)
    }

    private fun updateFCMToken(isFirstLaunch: Boolean) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val token = task.result

            if (task.isSuccessful) {
                if (isFirstLaunch) {
                    homeViewModel.saveFCMToken(token)
                    return@addOnCompleteListener
                }
                homeViewModel.updateFCMToken(token)
            }
        }
    }

    private fun setupDotsIndicator() {
        binding.dotsIndicatorHome.attachTo(binding.vpHomePopularFeed)
    }

    private fun onPopularNovelClick(novelId: Long) {
        tracker.trackEvent("home_today_ranking")
        navigateToNovelDetail(novelId)
    }

    private fun onUserInterestNovelFeedClick(novelId: Long) {
        tracker.trackEvent("home_love_feedlist")
        navigateToNovelDetail(novelId)
    }

    private fun onRecommendedNovelClick(novelId: Long) {
        tracker.trackEvent("home_prefer_novellist")
        navigateToNovelDetail(novelId)
    }

    private fun navigateToNovelDetail(novelId: Long) {
        startActivityLauncher.launch(
            NovelDetailActivity.getIntent(
                requireContext(),
                novelId,
            ),
        )
    }

    private fun onPopularFeedClick(feedId: Long) {
        tracker.trackEvent("home_hot_feedlist")
        navigateToFeedDetail(feedId)
    }

    private fun navigateToFeedDetail(feedId: Long) {
        startActivityLauncher.launch(
            FeedDetailActivity.getIntent(
                requireContext(),
                feedId,
            ),
        )
    }

    private fun onPostInterestNovelClick() {
        binding.clHomeInterestFeed.setOnClickListener {
            tracker.trackEvent("home_to_love_btn")
            startActivityLauncher.launch(
                NormalExploreActivity.getIntent(
                    requireContext(),
                ),
            )
        }
    }

    private fun onSettingPreferenceGenreClick() {
        binding.clHomeRecommendNovel.setOnClickListener {
            tracker.trackEvent("home_to_prefer_btn")
            startActivityLauncher.launch(
                ProfileEditActivity.getIntent(
                    requireContext(),
                ),
            )
        }
    }

    private fun onNotificationButtonClick() {
        binding.ivHomeNotification.setOnClickListener {
            val intent = NotificationActivity.getIntent(requireContext())
            homeResultLauncher.launch(intent)
        }
    }

    private fun showTermsAgreementDialog() {
        val dialog = TermsAgreementDialogFragment.newInstance()
        dialog.show(parentFragmentManager, TermsAgreementDialogFragment.TERMS_AGREEMENT_TAG)
    }

    companion object {
        private const val TODAY_POPULAR_NOVEL_MARGIN = 15
        private const val USER_INTEREST_MARGIN = 14
    }
}
