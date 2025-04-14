package com.into.websoso.ui.main.library

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.ui.model.ResultFrom
import com.into.websoso.databinding.FragmentLibraryBinding
import com.into.websoso.ui.main.MainActivity
import com.into.websoso.ui.main.library.model.LibraryUiState
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.into.websoso.ui.userStorage.SortMenuHandler
import com.into.websoso.ui.main.library.adapter.LibraryViewPagerAdapter
import com.into.websoso.ui.userStorage.model.StorageTab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseFragment<FragmentLibraryBinding>(R.layout.fragment_library) {
    private val libraryViewModel: LibraryViewModel by viewModels()

    private val libraryAdapter: LibraryViewPagerAdapter by lazy {
        LibraryViewPagerAdapter(
            novels = emptyList(),
            novelClickListener = ::navigateToNovelDetail,
        )
    }

    private val novelDetailResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ResultFrom.NovelDetailBack.RESULT_OK) {
                val currentReadStatus =
                    libraryViewModel.uiState.value?.readStatus ?: StorageTab.INTEREST.readStatus
                libraryViewModel.updateReadStatus(currentReadStatus, forceLoad = true)
            }
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        setupViewPagerAndTabLayout()
        setupObserver()
        setupInitialReadStatusTab()
        onSortTypeButtonClick()
        onExploreButtonClick()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.libraryViewModel = libraryViewModel
        binding.onSortTypeClick = ::onSortTypeButtonClick
    }

    private fun setupViewPagerAndTabLayout() {
        setupViewPager()
        setupTabLayoutWithViewPager()
    }

    private fun setupViewPager() {
        binding.vpLibrary.adapter = libraryAdapter
    }

    private fun setupInitialReadStatusTab() {
        val readStatus = StorageTab.INTEREST.readStatus
        libraryViewModel.updateReadStatus(readStatus, forceLoad = true)

        val initialTabIndex = StorageTab.entries.indexOfFirst { it.readStatus == readStatus }
        binding.tlLibrary.selectTab(binding.tlLibrary.getTabAt(initialTabIndex.coerceAtLeast(0)))
    }

    private fun setupTabLayoutWithViewPager() {
        TabLayoutMediator(binding.tlLibrary, binding.vpLibrary) { tab, position ->
            tab.text = StorageTab.fromPosition(position).title
        }.attach()

        binding.tlLibrary.addOnTabSelectedListener(
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
        libraryViewModel.updateReadStatus(
            selectedTab.readStatus,
            forceLoad = true,
        )
    }

    private fun setupObserver() {
        libraryViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.loading -> binding.wllLibrary.setWebsosoLoadingVisibility(true)
                uiState.error -> binding.wllLibrary.setLoadingLayoutVisibility(false)
                !uiState.loading -> {
                    binding.wllLibrary.setWebsosoLoadingVisibility(false)
                    updateStorageNovel(uiState)
                }
            }
            binding.clLibraryNull.visibility =
                if (uiState.userNovelCount == EMPTY_NOVEL_COUNT) View.VISIBLE else View.GONE

            binding.vpLibrary.visibility =
                if (uiState.userNovelCount > EMPTY_NOVEL_COUNT) View.VISIBLE else View.GONE

            binding.btnLibraryGoToSearchNovel.visibility =
                if (uiState.userNovelCount == EMPTY_NOVEL_COUNT) View.VISIBLE else View.GONE
        }

        libraryViewModel.isRatingChanged.observe(viewLifecycleOwner) { isChanged ->
            if (isChanged) {
                val currentReadStatus =
                    libraryViewModel.uiState.value?.readStatus ?: StorageTab.INTEREST.readStatus
                libraryViewModel.updateReadStatus(currentReadStatus, forceLoad = true)
                libraryViewModel.updateRatingChanged()
            }
        }
    }

    private fun updateStorageNovel(uiState: LibraryUiState) {
        if (uiState.storageNovels.isNotEmpty()) {
            libraryAdapter.updateNovels(uiState.storageNovels)
        }
    }

    private fun onExploreButtonClick() {
        binding.btnLibraryGoToSearchNovel.setOnClickListener {
            navigateToExploreFragment()
        }
    }

    private fun navigateToExploreFragment() {
        startActivity(MainActivity.getIntent(requireContext(), MainActivity.FragmentType.EXPLORE))
    }

    private fun navigateToNovelDetail(novelId: Long) {
        val intent = NovelDetailActivity.getIntent(requireContext(), novelId)
        novelDetailResultLauncher.launch(intent)
    }

    private fun onSortTypeButtonClick() {
        val sortMenuHandler = SortMenuHandler { sortType ->
            libraryViewModel.updateSortType(sortType)
        }

        binding.clLibrarySort.setOnClickListener { view ->
            sortMenuHandler.showSortMenu(view)
        }
    }

    companion object {
        const val EMPTY_NOVEL_COUNT = 0L
        const val READ_STATUS = "read_status"
    }
}
