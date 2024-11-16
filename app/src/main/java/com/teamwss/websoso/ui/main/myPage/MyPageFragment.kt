package com.teamwss.websoso.ui.main.myPage

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.common.ui.model.ResultFrom.ProfileEditSuccess
import com.teamwss.websoso.common.util.getS3ImageUrl
import com.teamwss.websoso.databinding.FragmentMyPageBinding
import com.teamwss.websoso.ui.main.MainViewModel
import com.teamwss.websoso.ui.main.myPage.adapter.MyPageViewPagerAdapter
import com.teamwss.websoso.ui.profileEdit.ProfileEditActivity
import com.teamwss.websoso.ui.setting.SettingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val myPageViewModel: MyPageViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewPagerAdapter by lazy { MyPageViewPagerAdapter(this) }

    private val startActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            ProfileEditSuccess.RESULT_OK -> {
                myPageViewModel.updateUserProfile()
                mainViewModel.updateUserInfo()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        setupViewPager()
        setupItemVisibilityOnToolBar()
        onSettingButtonClick()
        setupObserver()
        onProfileEditClick()
    }

    private fun bindViewModel() {
        binding.myPageViewModel = myPageViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupViewPager() {
        val tabTitleItems =
            listOf(getText(R.string.my_page_library), getText(R.string.my_page_activity))
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
            val color = if (isCollapsed) R.color.white else R.color.transparent
            tbMyPage.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    color,
                )
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
                uiState.error -> binding.wllMyPage.setLoadingLayoutVisibility(false)
                !uiState.loading -> binding.wllMyPage.setWebsosoLoadingVisibility(false)
            }
        }

        myPageViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                !uiState.loading -> setUpMyProfileImage(uiState.myProfile?.avatarImage.orEmpty())
                uiState.error -> Unit
            }
        }
    }

    private fun setUpMyProfileImage(myProfileUrl: String) {
        val updatedMyProfileImageUrl = binding.root.getS3ImageUrl(myProfileUrl)
        binding.ivMyPageUserProfile.load(updatedMyProfileImageUrl) {
            crossfade(true)
            error(R.drawable.img_loading_thumbnail)
            transformations(CircleCropTransformation())
        }
    }

    private fun onProfileEditClick() {
        binding.ivMyPageUserProfileEdit.setOnClickListener {
            startActivityLauncher.launch(
                ProfileEditActivity.getIntent(
                    requireContext(),
                )
            )
        }
    }

    companion object {
        private const val TOOLBAR_COLLAPSE_THRESHOLD = 0
    }
}
