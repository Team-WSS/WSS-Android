package com.into.websoso.ui.userStorage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.model.ResultFrom
import com.into.websoso.databinding.ActivityStorageBinding
import com.into.websoso.ui.main.MainActivity
import com.into.websoso.ui.main.myPage.myLibrary.MyLibraryFragment.Companion.EXTRA_SOURCE
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.into.websoso.ui.userStorage.adapter.UserStorageViewPagerAdapter
import com.into.websoso.ui.userStorage.model.StorageTab
import com.into.websoso.ui.userStorage.model.UserStorageUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserStorageActivity : BaseActivity<ActivityStorageBinding>(R.layout.activity_storage) {
    private val userStorageViewModel: UserStorageViewModel by viewModels()
    private val userStorageAdapter: UserStorageViewPagerAdapter by lazy {
        UserStorageViewPagerAdapter(
            novels = emptyList(),
            novelClickListener = ::navigateToNovelDetail,
        )
    }
    private val novelDetailResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ResultFrom.NovelDetailBack.RESULT_OK) {
                val currentReadStatus =
                    userStorageViewModel.uiState.value?.readStatus ?: StorageTab.INTEREST.readStatus
                userStorageViewModel.updateReadStatus(currentReadStatus, forceLoad = true)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUserId()
        bindViewModel()
        setupViewPagerAndTabLayout()
        setupInitialReadStatusTab()
        onBackButtonClick()
        onSortTypeButtonClick()
        onExploreButton()
    }

    private fun setupUserId() {
        val source = intent.getStringExtra(EXTRA_SOURCE) ?: SOURCE_MY_LIBRARY
        val userId = intent.getLongExtra(USER_ID_KEY, UserStorageViewModel.DEFAULT_USER_ID)
        val readStatus = intent.getStringExtra(READ_STATUS) ?: StorageTab.INTEREST.readStatus

        userStorageViewModel.updateUserStorage(source, userId, readStatus)
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.storageViewModel = userStorageViewModel
        binding.onSortTypeClick = ::onSortTypeButtonClick
    }

    private fun setupViewPagerAndTabLayout() {
        setupViewPager()
        setupTabLayoutWithViewPager()
        setupObserver()
    }

    private fun setupViewPager() {
        binding.vpStorage.adapter = userStorageAdapter
    }

    private fun setupInitialReadStatusTab() {
        val readStatus = intent.getStringExtra(READ_STATUS) ?: StorageTab.INTEREST.readStatus
        userStorageViewModel.updateReadStatus(readStatus, forceLoad = true)

        val initialTabIndex = StorageTab.entries.indexOfFirst { it.readStatus == readStatus }
        binding.tlStorage.selectTab(binding.tlStorage.getTabAt(initialTabIndex.coerceAtLeast(0)))
    }

    private fun setupTabLayoutWithViewPager() {
        TabLayoutMediator(binding.tlStorage, binding.vpStorage) { tab, position ->
            tab.text = StorageTab.fromPosition(position).title
        }.attach()

        binding.tlStorage.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val selectedTab = requireNotNull(tab) { "Tab must not be null" }
                    onReadingStatusTabSelected(selectedTab.position)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            },
        )
    }

    private fun onReadingStatusTabSelected(position: Int) {
        val selectedTab = StorageTab.fromPosition(position)
        userStorageViewModel.updateReadStatus(
            selectedTab.readStatus,
            forceLoad = true,
        )
    }

    private fun setupObserver() {
        userStorageViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.loading -> binding.wllStorage.setWebsosoLoadingVisibility(true)
                uiState.error -> binding.wllStorage.setLoadingLayoutVisibility(false)
                !uiState.loading -> {
                    binding.wllStorage.setWebsosoLoadingVisibility(false)
                    updateStorageNovel(uiState)
                }
            }

            binding.clStorageNull.visibility =
                if (uiState.userNovelCount == 0L) View.VISIBLE else View.GONE
            binding.vpStorage.visibility =
                if (uiState.userNovelCount > 0L) View.VISIBLE else View.GONE

            binding.btnStorageGoToSearchNovel.visibility =
                if (uiState.userNovelCount == 0L && intent.getStringExtra(EXTRA_SOURCE) == SOURCE_MY_LIBRARY) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        userStorageViewModel.isRatingChanged.observe(this) { isChanged ->
            if (isChanged) {
                val currentReadStatus =
                    userStorageViewModel.uiState.value?.readStatus ?: StorageTab.INTEREST.readStatus
                userStorageViewModel.updateReadStatus(currentReadStatus, forceLoad = true)
                userStorageViewModel.updateRatingChanged()
            }
        }
    }

    private fun updateStorageNovel(uiState: UserStorageUiState) {
        if (uiState.storageNovels.isNotEmpty()) {
            userStorageAdapter.updateNovels(uiState.storageNovels)
        }
    }

    private fun onExploreButton() {
        binding.btnStorageGoToSearchNovel.setOnClickListener {
            navigateToExploreFragment()
        }
    }

    private fun navigateToExploreFragment() {
        startActivity(MainActivity.getIntent(this, MainActivity.FragmentType.EXPLORE))
    }

    private fun navigateToNovelDetail(novelId: Long) {
        val intent = NovelDetailActivity.getIntent(this, novelId)
        novelDetailResultLauncher.launch(intent)
    }

    private fun onBackButtonClick() {
        binding.ivStorageDetailBackButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun onSortTypeButtonClick() {
        val sortMenuHandler = SortMenuHandler { sortType ->
            userStorageViewModel.updateSortType(sortType)
        }

        binding.clStorageSort.setOnClickListener { view ->
            sortMenuHandler.showSortMenu(view)
        }
    }

    companion object {
        const val USER_ID_KEY = "userId"
        const val SOURCE_MY_LIBRARY = "myLibrary"
        const val SOURCE_OTHER_USER_LIBRARY = "otherUserLibrary"
        const val READ_STATUS = "read_status"

        fun getIntent(
            context: Context,
            source: String,
            userId: Long,
            readStatus: String = StorageTab.INTEREST.readStatus,
        ): Intent
        = Intent(context, UserStorageActivity::class.java).apply {
            putExtra(EXTRA_SOURCE, source)
            putExtra(USER_ID_KEY, userId)
            putExtra(READ_STATUS, readStatus)
        }
    }
}
