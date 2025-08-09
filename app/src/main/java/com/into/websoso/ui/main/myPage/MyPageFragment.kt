package com.into.websoso.ui.main.myPage

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.tabs.TabLayoutMediator
import com.into.websoso.R.color.transparent
import com.into.websoso.R.color.white
import com.into.websoso.R.layout.fragment_my_page
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.ui.model.ResultFrom.ProfileEditSuccess
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.resource.R.drawable.img_loading_thumbnail
import com.into.websoso.core.resource.R.string.my_page_activity
import com.into.websoso.core.resource.R.string.my_page_library
import com.into.websoso.databinding.FragmentMyPageBinding
import com.into.websoso.ui.main.MainViewModel
import com.into.websoso.ui.main.myPage.adapter.MyPageViewPagerAdapter
import com.into.websoso.ui.profileEdit.ProfileEditActivity
import com.into.websoso.ui.setting.SettingActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(fragment_my_page) {
    @Inject
    lateinit var tracker: Tracker

    private val myPageViewModel: MyPageViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewPagerAdapter by lazy { MyPageViewPagerAdapter(this) }

    private val startActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        when (result.resultCode) {
            ProfileEditSuccess.RESULT_OK -> {
                myPageViewModel.updateUserProfile()
                mainViewModel.updateUserInfo()
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        setupViewPager()
        setupItemVisibilityOnToolBar()
        onSettingButtonClick()
        setupObserver()
        onProfileEditClick()
        tracker.trackEvent("mypage")
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.updateUserInfo()
        myPageViewModel.updateUserProfile()
    }

    private fun bindViewModel() {
        binding.myPageViewModel = myPageViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupViewPager() {
        val tabTitleItems =
            listOf(getText(my_page_library), getText(my_page_activity))
        binding.vpMyPage.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tlMyPage, binding.vpMyPage) { tab, position ->
            tab.text = tabTitleItems[position]
        }.attach()
    }

    private fun setupItemVisibilityOnToolBar() {
        binding.ablMyPage.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = (totalScrollRange.toFloat() + verticalOffset) / totalScrollRange
            updateToolbarUi(percentage <= TOOLBAR_COLLAPSE_THRESHOLD)
        }
    }

    private fun updateToolbarUi(isCollapsed: Boolean) {
        with(binding) {
            val color = if (isCollapsed) white else transparent
            tbMyPage.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    color,
                ),
            )

            tvMyPageStickyTitle.isVisible = isCollapsed
            clMyPageUserProfile.isVisible = !isCollapsed
        }
    }

    private fun onSettingButtonClick() {
        binding.ivMyPageStickyGoToSetting.setOnClickListener {
            val intent = SettingActivity.getIntent(requireContext())
            startActivity(intent)
        }
    }

    private fun setupObserver() {
        myPageViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.loading -> binding.wllMyPage.setWebsosoLoadingVisibility(true)
                uiState.error -> binding.wllMyPage.setErrorLayoutVisibility(true)
                !uiState.loading -> {
                    binding.wllMyPage.setErrorLayoutVisibility(false)
                    binding.wllMyPage.setWebsosoLoadingVisibility(false)
                    setUpMyProfileImage(uiState.myProfile?.avatarImage.orEmpty())
                }
            }
        }
    }

    private fun setUpMyProfileImage(myProfileUrl: String) {
        val updatedMyProfileImageUrl = binding.root.getS3ImageUrl(myProfileUrl)
        binding.ivMyPageUserProfile.load(updatedMyProfileImageUrl) {
            crossfade(true)
            error(img_loading_thumbnail)
            transformations(CircleCropTransformation())
        }
    }

    private fun onProfileEditClick() {
        binding.ivMyPageUserProfileEdit.setOnClickListener {
            startActivityLauncher.launch(
                ProfileEditActivity.getIntent(
                    requireContext(),
                ),
            )
        }
    }

    companion object {
        private const val TOOLBAR_COLLAPSE_THRESHOLD = 0
        const val TAG = "MyPageFragment"
    }
}
